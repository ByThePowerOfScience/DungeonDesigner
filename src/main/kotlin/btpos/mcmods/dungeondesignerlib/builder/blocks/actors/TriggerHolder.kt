@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package btpos.mcmods.dungeondesignerlib.builder.blocks.actors

import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.ext.vanilla.data.getCompoundOrNull
import btpos.mcmods.devutil.common.ext.vanilla.world.blockEntity
import btpos.mcmods.devutil.common.ext.vanilla.world.getMaxCornerBlock
import btpos.mcmods.devutil.common.ext.vanilla.world.getMinCornerBlock
import btpos.mcmods.devutil.common.ext.vanilla.world.with
import btpos.mcmods.devutil.common.structure.ITileState
import btpos.mcmods.devutil.common.ext.vanilla.plus
import btpos.mcmods.devutil.common.macros.ChatUtils.toComponent
import btpos.mcmods.devutil.common.util.serialization.ICodecSerializable
import btpos.mcmods.devutil.common.util.serialization.putNbtSerializable
import btpos.mcmods.devutil.common.util.serialization.readNbtSerializableToExisting
import btpos.mcmods.devutil.forge.datagen.IBlockDataGen
import btpos.mcmods.devutil.forge.datagen.variantDsl
import btpos.mcmods.devutil.parts.IItemRepresentable_Tag
import btpos.mcmods.devutil.parts.dropItemInWorld
import btpos.mcmods.dungeondesignerlib.WorldUtils
import btpos.mcmods.dungeondesignerlib.POWERED
import btpos.mcmods.dungeondesignerlib.builder.items.ItemTriggerVariable
import btpos.mcmods.dungeondesignerlib.registry.ModBlocks
import btpos.mcmods.dungeondesignerlib.registry.ModItems
import btpos.mcmods.dungeondesignerlib.builder.nbt.TriggerBoundsTag
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
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
import net.minecraftforge.client.model.generators.BlockStateProvider
import java.util.UUID

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
		
		if (pLevel.isClientSide) {
			return InteractionResult.SUCCESS
		}
		
		val ourEnt = pLevel.blockEntity(pPos, ModBlocks.TRIGGER_BLOCK_ENTITY) ?: return InteractionResult.PASS
		
		// Pop out trigger item into world if it exists
		if (pPlayer.isShiftKeyDown) {
			ourEnt.state.triggerDelegate.dropItemInWorld(pLevel, pPos)
			return InteractionResult.SUCCESS
		}
		
		// Else add it to the block
		if (itemInHand.`is`(ModItems.TRIGGER_ITEM) && ourEnt.state.trigger == null) {
			ourEnt.state.triggerDelegate.asItem = itemInHand
			itemInHand.shrink(1)
			
			return InteractionResult.CONSUME
		}
		
		// Finally, if no other action has occurred, show the bounds.
		if (ourEnt.state.trigger == null) {
			pPlayer.sendSystemMessage("No bbox".asComponent())
		} else {
			pPlayer.sendSystemMessage(
					Component.literal("Trigger bounds: ")
					+ ourEnt.state.trigger!!.getMinCornerBlock().toComponent().withStyle(ChatFormatting.YELLOW)
					+ " to "
					+ ourEnt.state.trigger!!.getMaxCornerBlock().toComponent().withStyle(ChatFormatting.YELLOW)
			)
		}
		
		return InteractionResult.CONSUME
	}
	
	override fun <T : BlockEntity> getTicker(pLevel: Level, pState: BlockState, pBlockEntityType: BlockEntityType<T>): BlockEntityTicker<T>? {
		return BlockEntityTicker { level, pos, state, ent ->
			if (ent !is TileTriggerHolder || ent.state.trigger == null)
				return@BlockEntityTicker
			
			val isPowered = state.getValue(POWERED)
			
			if (shouldStopCheckingTrigger(level, pos)) {
				// Stop ticking if receiving redstone power from above
				return@BlockEntityTicker
			}
			
			if (isPowered != WorldUtils.isPlayerInBoundingBox(ent.state.trigger!!, level)) {
				level.setBlockAndUpdate(pos, state.with(POWERED, !isPowered))
			}
		}
	}
	
	private fun shouldStopCheckingTrigger(level: Level, pos: BlockPos): Boolean = level.hasSignal(pos, Direction.UP)
}

class TileTriggerHolder(p0: BlockPos, p1: BlockState) : BlockEntity(ModBlocks.TRIGGER_BLOCK_ENTITY, p0, p1) {
	class TriggerHolderState(
		pTrigger: AABB? = null,
		pPlacer: UUID? = null,
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
		
		var triggerDelegate = object : IItemRepresentable_Tag<AABB> {
			override val defaultItem: Item
				get() = ModItems.TRIGGER_ITEM
			
			override var value: AABB? by notify(pTrigger)
			
			override fun CompoundTag.readFromTag(): AABB? {
				return this.getCompoundOrNull(ItemTriggerVariable.TAGKEY_STATE)?.let(::TriggerBoundsTag)?.toAABB()
			}
			
			override fun CompoundTag.writeToTag(value: AABB) {
				this.put(ItemTriggerVariable.TAGKEY_STATE, TriggerBoundsTag(value).tag)
			}
		}
		
		var trigger: AABB? by triggerDelegate::value
		var placer: UUID? by notify(pPlacer)
		
		companion object {
			const val TAGKEY_BOUNDS = "trigger"
			const val TAGKEY_PLACER = "placer"
			
			val CODEC = RecordCodecBuilder.create<TriggerHolderState> { inst ->
				inst.group(
						TriggerBoundsTag.CODEC.optionalFieldOf(TAGKEY_BOUNDS, null)
							.forGetter(TriggerHolderState::trigger),
						UUIDUtil.CODEC.optionalFieldOf(TAGKEY_PLACER, null).forGetter(TriggerHolderState::placer)
				).apply(inst, ::TriggerHolderState)
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
}
