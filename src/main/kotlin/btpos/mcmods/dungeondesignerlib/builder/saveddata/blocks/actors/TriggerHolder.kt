@file:Suppress("OVERRIDE_DEPRECATION")

package btpos.mcmods.dungeondesignerlib.builder.saveddata.blocks.actors

import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.ext.vanilla.blockEntity
import btpos.mcmods.devutil.common.ext.vanilla.dropItem
import btpos.mcmods.devutil.common.ext.vanilla.getMaxCornerBlock
import btpos.mcmods.devutil.common.ext.vanilla.getMinCornerBlock
import btpos.mcmods.devutil.common.ext.vanilla.stack
import btpos.mcmods.devutil.common.ext.vanilla.with
import btpos.mcmods.devutil.common.structure.ITileState
import btpos.mcmods.devutil.common.util.ChatUtils
import btpos.mcmods.devutil.common.util.plus
import btpos.mcmods.devutil.common.util.serialization.ICodecSerializable
import btpos.mcmods.devutil.common.util.serialization.Serialization
import btpos.mcmods.devutil.common.util.serialization.putNbtSerializable
import btpos.mcmods.devutil.common.util.serialization.readNbtSerializableToExisting
import btpos.mcmods.devutil.forge.datagen.IBlockDataGen
import btpos.mcmods.devutil.forge.datagen.variantDsl
import btpos.mcmods.dungeondesignerlib.WorldUtils
import btpos.mcmods.dungeondesignerlib.IMMUTABLE
import btpos.mcmods.dungeondesignerlib.POWERED
import btpos.mcmods.dungeondesignerlib.builder.items.ItemTriggerVariable
import btpos.mcmods.dungeondesignerlib.registry.ModBlocks
import btpos.mcmods.dungeondesignerlib.registry.ModItems
import btpos.mcmods.dungeondesignerlib.builder.saveddata.TriggerBoundsTag
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
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
import net.minecraft.world.phys.Vec3
import net.minecraftforge.client.model.generators.BlockStateProvider
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3
import java.util.UUID

class BlockTriggerHolder(
	props: Properties
) : Block(props), EntityBlock {
	init {
		registerDefaultState(stateDefinition.any().with(POWERED, false).with(IMMUTABLE, false))
	}
	
	companion object : IBlockDataGen {
		override val id get() = "trigger_holder"
		
		// TODO: Replace dev textures with something we actually own
		const val TEXTURE_TOP_BOTTOM = "logic_programmer_top"
		const val TEXTURE_SIDES = "logic_programmer_side"
		const val TEXTURE_ON_TOP_BOTTOM = "logic_programmer_top_on"
		const val TEXTURE_ON_SIDES = "logic_programmer_side_on"
		
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
		pBuilder.add(POWERED, IMMUTABLE)
	}
	
	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? = ModBlocks.TRIGGER_BLOCK_ENTITY.create(pos, state)
	//endregion
	
	override fun setPlacedBy(pLevel: Level, pPos: BlockPos,
	                         pState: BlockState,
	                         pPlacer: LivingEntity?,
	                         pStack: ItemStack
	) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack)
		
		if (pPlacer == null || pPlacer !is Player) {
			TODO("Make it fail to place")
		}
		
		pLevel.blockEntity(pPos, ModBlocks.TRIGGER_BLOCK_ENTITY)?.run {
			state.placer = pPlacer.uuid
		}
	}
	
	override fun use(pState: BlockState, pLevel: Level,
	                 pPos: BlockPos, pPlayer: Player,
	                 pHand: InteractionHand, pHit: BlockHitResult
	): InteractionResult {
		val itemInHand = pPlayer.getItemInHand(pHand)
		
		if (pLevel.isClientSide) {
			return InteractionResult.SUCCESS
		}
		
		
		val ourEnt = pLevel.blockEntity(pPos, ModBlocks.TRIGGER_BLOCK_ENTITY) ?: return InteractionResult.PASS
		
		// Pop out trigger item into world if it exists
		if (pPlayer.isShiftKeyDown) {
			dropTriggerItem(ourEnt, pLevel, pPos)
			return InteractionResult.SUCCESS
		}
		
		// Else add it to the block
		if (itemInHand.`is`(ModItems.TRIGGER_ITEM)) {
			dropTriggerItem(ourEnt, pLevel, pPos)
			
			ourEnt.triggerItem = itemInHand
			if (ourEnt.state.trigger != null)
				itemInHand.shrink(1)
			
			return InteractionResult.CONSUME
		}
		
		// Finally, if no other action has occurred, show the bounds.
		if (ourEnt.state.trigger == null) {
			pPlayer.sendSystemMessage("No bbox".asComponent())
		} else {
			pPlayer.sendSystemMessage(
					Component.literal("Trigger bounds: ")
					+ ChatUtils.toComponent(ourEnt.state.trigger!!.getMinCornerBlock())
					+ " to "
					+ ChatUtils.toComponent(ourEnt.state.trigger!!.getMaxCornerBlock())
			)
			
		}
		
		
		
		return InteractionResult.PASS
	}
	
	override fun <T : BlockEntity> getTicker(pLevel: Level, pState: BlockState, pBlockEntityType: BlockEntityType<T>): BlockEntityTicker<T>? {
		return BlockEntityTicker { level, pos, state, ent ->
			if (ent !is TileTriggerHolder || ent.state.trigger == null)
				return@BlockEntityTicker
			
			when (state.getValue(POWERED)) {
				false -> {
					if (WorldUtils.isPlayerInBoundingBox(ent.state.trigger!!, level)) {
						level.setBlockAndUpdate(pos, state.with(POWERED, true))
					}
				}
				true -> {
					if (!WorldUtils.isPlayerInBoundingBox(ent.state.trigger!!, level)) {
						level.setBlockAndUpdate(pos, state.with(POWERED, false))
					}
				}
			}
		}
	}
	
	/**
	 * Drops the current trigger as an item in the world
	 */
	private fun dropTriggerItem(ourEnt: TileTriggerHolder, pLevel: Level, pPos: BlockPos) {
		ourEnt.triggerItem.let {
			if (!it.isEmpty) {
				pLevel.dropItem(it, pPos.above().toVec3(), Vec3(0.0, 0.1, 0.0))
				ourEnt.state.trigger = null
			}
		}
	}
}

class TileTriggerHolder(p0: BlockPos, p1: BlockState) : BlockEntity(ModBlocks.TRIGGER_BLOCK_ENTITY, p0, p1) {
	
	class TriggerHolderState(
		trigger: AABB? = null,
		placer: UUID? = null,
		override val onChange: () -> Unit = {}
	)
		: ITileState, ICodecSerializable<TriggerHolderState>
	{
		//region Codec
		override fun codec() = CODEC
		override fun copyFrom(other: TriggerHolderState) {
			this.trigger = other.trigger
			this.placer = other.placer
		}
		//endregion
		
		var trigger: AABB? by notify(trigger)
		var placer: UUID? by notify(placer)
		
		companion object {
			const val TAGKEY_BOUNDS = "trigger"
			const val TAGKEY_PLACER = "placer"
			val CODEC = RecordCodecBuilder.create<TriggerHolderState> {
				it.group(
						TriggerBoundsTag.CODEC.optionalFieldOf(TAGKEY_BOUNDS, null)
							.forGetter(TriggerHolderState::trigger),
						UUIDUtil.CODEC.optionalFieldOf(TAGKEY_PLACER, null).forGetter(TriggerHolderState::placer)
				).apply(it, ::TriggerHolderState)
			}
		}
	}
	
	companion object {
		const val TAGKEY_TRIGGER = "trigger"
	}
	
	val state = TriggerHolderState(onChange=this::setChanged)
	
	override fun saveAdditional(tag: CompoundTag) {
		super.saveAdditional(tag)
		tag.putNbtSerializable(TAGKEY_TRIGGER, state)
	}
	
	override fun load(tag: CompoundTag) {
		super.load(tag)
		tag.readNbtSerializableToExisting(TAGKEY_TRIGGER, state)
	}
	
	/**
	 * Helper property to set/get the trigger from/as an [ItemTriggerVariable].
	 */
	var triggerItem: ItemStack
		get() {
			with(state) {
				if (trigger == null) {
					return ItemStack.EMPTY
				} else {
					return ModItems.TRIGGER_ITEM.stack().apply {
						getOrCreateTag().put(ItemTriggerVariable.STATE, TriggerBoundsTag(trigger!!).tag)
					}
				}
			}
		}
		set(stack) {
			with(state) {
				if (stack.isEmpty) {
					this.trigger = null
					return
				}
				if (!stack.`is`(ModItems.TRIGGER_ITEM))
					return;
				
				this.trigger = stack.getTagElement(ItemTriggerVariable.STATE)
					               ?.let(::TriggerBoundsTag)
					               ?.toAABB() ?: return
			}
		}
}
