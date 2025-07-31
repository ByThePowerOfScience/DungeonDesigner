@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package btpos.mcmods.dungeondesignerlib.builder.blocks.actors

import btpos.mcmods.devutil.common.ext.kotlin.isNullOrTrue
import btpos.mcmods.devutil.common.ext.kotlin.safeGetDelegate
import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.ext.vanilla.data.nullableFieldOf
import btpos.mcmods.devutil.common.ext.vanilla.destructuring.component1
import btpos.mcmods.devutil.common.ext.vanilla.destructuring.component2
import btpos.mcmods.devutil.common.ext.vanilla.world.blockEntity
import btpos.mcmods.devutil.common.ext.vanilla.world.with
import btpos.mcmods.devutil.common.ext.vanilla.plus
import btpos.mcmods.devutil.common.ext.vanilla.world.BlockInclusiveAABB
import btpos.mcmods.devutil.common.ext.vanilla.world.BlockInclusiveAABB.Companion.toBlockInclusive
import btpos.mcmods.devutil.common.ext.vanilla.world.dropItemAboveBlock
import btpos.mcmods.devutil.common.ext.vanilla.world.runOnServer
import btpos.mcmods.devutil.common.macros.ChatUtils.toComponent
import btpos.mcmods.devutil.common.structure.IOnChange
import btpos.mcmods.devutil.common.util.serialization.ICodecSerializable
import btpos.mcmods.devutil.common.util.serialization.Serialization
import btpos.mcmods.devutil.common.util.serialization.putNbtSerializable
import btpos.mcmods.devutil.common.util.serialization.readNbtSerializableToExisting
import btpos.mcmods.devutil.forge.datagen.IBlockDataGen
import btpos.mcmods.devutil.forge.datagen.variantDsl
import btpos.mcmods.devutil.parts.IItemRepresentable_Tag
import btpos.mcmods.devutil.util.properties.LazyCache
import btpos.mcmods.dungeondesignerlib.WorldUtils
import btpos.mcmods.dungeondesignerlib.POWERED
import btpos.mcmods.dungeondesignerlib.builder.items.ItemTriggerVariable
import btpos.mcmods.dungeondesignerlib.registry.ModBlocks
import btpos.mcmods.dungeondesignerlib.registry.ModItems
import btpos.mcmods.dungeondesignerlib.common.nbtadapters.DisplayNameGetter
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.client.model.generators.BlockStateProvider
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3
import kotlin.jvm.optionals.getOrNull

class BlockTriggerHolder(
	props: Properties
) : Block(props), EntityBlock {
	init {
		registerDefaultState(stateDefinition.any().with(POWERED, false))
	}
	
	companion object : IBlockDataGen {
		override val id get() = "trigger_holder"
		
		// TODO: Replace dev textures with something we actually own
		const val TEXTURE_TOP_BOTTOM = "logic_programmer_top"
		const val TEXTURE_SIDES = "logic_programmer_side"
		const val TEXTURE_ON_TOP_BOTTOM = "logic_programmer_top_on"
		const val TEXTURE_ON_SIDES = "logic_programmer_side_on"
		
		@Suppress("DuplicatedCode")
		override fun BlockStateProvider.buildModelsAndStates() {
			val off = blockLoc(TEXTURE_SIDES).let { side ->
				blockLoc(TEXTURE_TOP_BOTTOM).let { updown ->
					models().cube(id, updown, updown, side, side, side, side)
				}
			}
			val on = blockLoc(TEXTURE_ON_SIDES).let { side ->
				blockLoc(TEXTURE_ON_TOP_BOTTOM).let { updown ->
					models().cube("${id}_on", updown, updown, side, side, side, side)
				}
			}
			
			variantDsl(ModBlocks.TRIGGER_BLOCK) {
				POWERED {
					false {
						model {
							modelFile(off)
						}
					}
					true {
						model {
							modelFile(on)
						}
					}
				}
			}
			
			simpleBlockItem(ModBlocks.TRIGGER_BLOCK, off)
		}
	}
	
	
	//region Redstone
	override fun isSignalSource(pState: BlockState) = true
	
	override fun getSignal(pState: BlockState, pLevel: BlockGetter, pPos: BlockPos, pDirection: Direction): Int {
		if (pState.getValue(POWERED))
			return 15
		else
			return 0
	}
	//endregion
	
	
	//region Configuration
	override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
		super.createBlockStateDefinition(pBuilder)
		pBuilder.add(POWERED)
	}
	
	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? = ModBlocks.TRIGGER_BLOCK_ENTITY.create(pos, state)
	//endregion
	
	
	override fun neighborChanged(pState: BlockState, pLevel: Level, pPos: BlockPos, pNeighborBlock: Block,
	                             pNeighborPos: BlockPos, pMovedByPiston: Boolean) {
		super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston)
		if (pState.getValue(POWERED) == true && shouldStopCheckingTrigger(pLevel, pPos)) {
			pLevel.setBlockAndUpdate(pPos, pState.with(POWERED, false))
		}
	}
	
	override fun use(pState: BlockState, pLevel: Level,
	                 pPos: BlockPos, pPlayer: Player,
	                 pHand: InteractionHand, pHit: BlockHitResult
	): InteractionResult {
		val itemInHand = pPlayer.getItemInHand(pHand)
		
		val ourEnt = pLevel.blockEntity(pPos, ModBlocks.TRIGGER_BLOCK_ENTITY) ?: return InteractionResult.PASS
		
		// Pop out trigger item into world if it exists
		if (pPlayer.isShiftKeyDown) {
            return pLevel.runOnServer {
                if (ourEnt.dropItem()) {
                    // idk man
                }
            }.sidedResult
		}
		
		// Else add it to the block
		if (itemInHand.`is`(ModItems.TRIGGER_ITEM) && !ourEnt.hasTrigger()) {
			return pLevel.runOnServer {
				ourEnt.setTriggerFromitem(itemInHand)
				if (ourEnt.hasTrigger())
					itemInHand.shrink(1)
			}.sidedResult
		}
		
		// Finally, if no other action has occurred, show the bounds.
		return pLevel.runOnServer {
			if (!ourEnt.hasTrigger()) {
				pPlayer.sendSystemMessage("No bbox".asComponent())
			} else {
                val (first, second) = ourEnt.triggerCorners!!
				pPlayer.sendSystemMessage(
					Component.literal("Trigger bounds: ")
							+ first.toComponent().withStyle(ChatFormatting.YELLOW)
							+ " to "
							+ second.toComponent().withStyle(ChatFormatting.YELLOW)
				)
			}
		}.sidedResult
	}
	
	override fun getDrops(pState: BlockState, pParams: LootParams.Builder): MutableList<ItemStack> {
		val sup = super.getDrops(pState, pParams)
		val item = (pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY) as? TileTriggerHolder)?.triggerItem ?: return sup
		sup += item
		return sup
	}
	
	override fun <T : BlockEntity> getTicker(pLevel: Level, pState: BlockState, pBlockEntityType: BlockEntityType<T>): BlockEntityTicker<T>? {
		return if (pLevel.isClientSide) null else BlockEntityTicker { level, pos, state, ent ->
			if (ent !is TileTriggerHolder || ent.triggerBoundingBox == null)
				return@BlockEntityTicker
			
			
			if (shouldStopCheckingTrigger(level, pos)) {
				// Don't tick if receiving redstone power from above
				return@BlockEntityTicker
			}
			
			val isPowered = state.getValue(POWERED)
			
			if (isPowered != WorldUtils.isPlayerInBoundingBox(ent.triggerBoundingBox!!, level)) {
				level.setBlockAndUpdate(pos, state.with(POWERED, !isPowered))
			}
		}
	}
	
	private fun shouldStopCheckingTrigger(level: Level, pos: BlockPos): Boolean = level.hasSignal(pos.above(), Direction.UP)
}

class TileTriggerHolder(p0: BlockPos, p1: BlockState) : BlockEntity(ModBlocks.TRIGGER_BLOCK_ENTITY, p0, p1) {
	companion object {
		private const val TAGKEY_STATE = "trigger"
	}
	
	private val state = TriggerHolderState(pOnChange=this::setChanged)
    
    val triggerCorners by state::corners
    val triggerBoundingBox get() = state.aabb?.bb
    
	var triggerItem: ItemStack
        get() = state.triggerDelegate.asItem
		set(value) {
            state.triggerDelegate.asItem = value
        }
	
    fun setTriggerFromitem(stack: ItemStack) {
        state.triggerDelegate.asItem = stack
    }
    
    fun hasTrigger() = state.hasTrigger()
    
    /**
     * Drops the trigger held by this blockentity as an [ItemTriggerVariable] in the world above this block.
     *
     * Return true if successfully dropped, false otherwise.
     */
    fun dropItem(): Boolean {
		if (!hasTrigger() || level?.isClientSide.isNullOrTrue())
			return false
		
		return (level as ServerLevel).dropItemAboveBlock(triggerItem, blockPos)
    }
	
	override fun saveAdditional(tag: CompoundTag) {
		super.saveAdditional(tag)
		tag.putNbtSerializable(TAGKEY_STATE, state)
	}
	
	override fun load(tag: CompoundTag) {
		super.load(tag)
		tag.readNbtSerializableToExisting(TAGKEY_STATE, state)
	}
}


class TriggerHolderState(
    pTrigger: Pair<BlockPos, BlockPos>? = null,
    pItemName: String? = null,
    pOnChange: () -> Unit = {}
) : IOnChange, ICodecSerializable<TriggerHolderState> {
    override var onChange = pOnChange
        set(callback) {
            field = callback
            triggerDelegate.onChange = callback
        }
    
    //region Codec
    override fun codec() = CODEC
    override fun copyFrom(other: TriggerHolderState) {
        this.triggerDelegate.copyFrom(other.triggerDelegate)
    }
    //endregion
    
    val triggerDelegate = TriggerVarItemConverter(pTrigger, pItemName, pOnChange)
    
    val corners get() = triggerDelegate.value
    val itemName get() = triggerDelegate.name
    val aabb get() = triggerDelegate.cachedInclusiveAABB
    
    
    companion object {
        const val TAGKEY_BOUNDS = "trigger"
        const val TAGKEY_NAME = "item_name"
        
        val CODEC = RecordCodecBuilder.create {
            it.group(
				Serialization.pairCodec(BlockPos.CODEC, BlockPos.CODEC).nullableFieldOf(TAGKEY_BOUNDS, TriggerHolderState::corners),
                Codec.STRING.nullableFieldOf(TAGKEY_NAME, TriggerHolderState::itemName)
            ).apply(
                it,
                { pTrigger, pItemName ->
                    TriggerHolderState(
                        pTrigger.getOrNull(),
                        pItemName.getOrNull()
                    )
                }
            )
        }
    }
    
    fun hasTrigger(): Boolean {
        return corners != null
    }
}

class TriggerVarItemConverter(triggerIn: Pair<BlockPos, BlockPos>? = null, nameIn: String? = null, override var onChange: () -> Unit = {}) : IOnChange, IItemRepresentable_Tag<Pair<BlockPos, BlockPos>> {
    override val defaultItem: Item
        get() = ModItems.TRIGGER_ITEM
    
    /**
     * A cache of the bounding box for the trigger we check every tick in [BlockTriggerHolder.getTicker].
     *
     * Derived from [value], and invalidated by [value]'s setter.
     */
    val cachedInclusiveAABB: BlockInclusiveAABB? by LazyCache { value?.run { AABB(first, second).toBlockInclusive() } }
    
    /**
     * Store the corners as BlockPos instead of the value as an AABB so we can return the item in the same way it was given,
     * since the AABB constructor changes the corners around.
     */
    override var value: Pair<BlockPos, BlockPos>? = triggerIn
        set(v) {
            onChange()
            ::cachedInclusiveAABB.safeGetDelegate<LazyCache<*>>()?.invalidate()
            field = v
        }
    
    /**
     * Also store the anvil name of the item so we can restore it with its name when it's popped out.
     *
     * We don't need to hook this up to [notify] since it's always set at the same time as [value] in [readFromTag].
     */
    var name: String? = nameIn
        private set
    
    override fun CompoundTag.readFromTag() {
        // Reject if trigger bounds is incomplete on this stack
        val tagData = ItemTriggerVariable.getTriggerBoundsNbt(this)?.let(ItemTriggerVariable::NbtAdapter)?.takeIf { it.isComplete() } ?: return
        val nameJson = DisplayNameGetter(this).nameJson
        
        value = Pair(tagData.first!!, tagData.second!!)
        name = nameJson
    }
    
    override fun CompoundTag.writeToTag() {
        DisplayNameGetter(this).nameJson = name
        
        val (vFirst, vSecond) = value!!
        ItemTriggerVariable.getOrCreateTriggerNbt(this).let(ItemTriggerVariable::NbtAdapter).run {
            first = vFirst
            second = vSecond
        }
    }
    
    fun copyFrom(other: TriggerVarItemConverter) {
        this.name = other.name
        this.value = other.value
    }
}