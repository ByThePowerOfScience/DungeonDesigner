@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package btpos.mcmods.dungeondesigner.builder.blocks.actors

import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.ext.vanilla.data.nullableFieldOf
import btpos.mcmods.devutil.common.ext.vanilla.plus
import btpos.mcmods.devutil.common.ext.vanilla.sendSystemMessage
import btpos.mcmods.devutil.common.ext.vanilla.stack
import btpos.mcmods.devutil.common.ext.vanilla.world.blockEntity
import btpos.mcmods.devutil.common.ext.vanilla.world.dropItemAboveBlock
import btpos.mcmods.devutil.common.ext.vanilla.world.runOnServer
import btpos.mcmods.devutil.common.ext.vanilla.world.runOnServerLevel
import btpos.mcmods.devutil.common.ext.vanilla.world.with
import btpos.mcmods.devutil.common.macros.ChatUtils
import btpos.mcmods.devutil.common.structure.blocks.IObjectData
import btpos.mcmods.devutil.common.structure.composition.IOnChange
import btpos.mcmods.devutil.common.util.serialization.ICodecSerializable
import btpos.mcmods.devutil.common.util.serialization.putCodecSerializable
import btpos.mcmods.devutil.common.util.serialization.readCodecSerializableToExisting
import btpos.mcmods.devutil.multiplatform.api.IPlatformConnectRedstone
import btpos.mcmods.devutil.parts.IItemRepresentable
import btpos.mcmods.dungeondesigner.POWERED
import btpos.mcmods.dungeondesigner.builder.world.dungeonBuilderData
import btpos.mcmods.dungeondesigner.common.nbtadapters.setDisplayName
import btpos.mcmods.dungeondesigner.compiled.saveddata.FlagName
import btpos.mcmods.dungeondesigner.registry.ModBlocks
import btpos.mcmods.dungeondesigner.registry.ModItems
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.redstone.Orientation
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.BlockHitResult
import kotlin.jvm.optionals.getOrNull

abstract class AbstractFlagHolderBlock(props: Properties) : Block(props), EntityBlock {
	init {
		registerDefaultState(stateDefinition.any().with(POWERED, false))
	}
	
	//region Configuration
	override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
		super.createBlockStateDefinition(pBuilder)
		pBuilder.add(POWERED)
	}
	
	override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity? = ModBlocks.FLAG_BLOCK_ENTITY.create(pPos, pState)
	//endregion
	
	//region Interaction
	override fun useItemOn(itemInHand: ItemStack, pState: BlockState, pLevel: Level, pPos: BlockPos, pPlayer: Player, pHand: InteractionHand, pHit: BlockHitResult): InteractionResult
	{
		val ourEnt = pLevel.blockEntity(pPos, ModBlocks.FLAG_BLOCK_ENTITY) ?: return InteractionResult.PASS
		
		// Pop out trigger item into world if it exists
		if (pPlayer.isShiftKeyDown && ourEnt.hasFlag()) {
			return pLevel.runOnServerLevel {
				dropItemAboveBlock(ourEnt.flagItem, pPos)
			}.sidedResult
		}
		
		// Else add it to the block
		if (itemInHand.`is`(ModItems.FLAG_ITEM) && !ourEnt.hasFlag()) {
			return pLevel.runOnServer {
				ourEnt.flagItem = itemInHand
				if (ourEnt.hasFlag())
					itemInHand.shrink(1)
			}.sidedResult
		}
		
		
		// Finally, if no other action has occurred, show the name of the flag.
		return pLevel.runOnServer {
			if (!ourEnt.hasFlag()) {
				pPlayer.sendSystemMessage("No flag set".asComponent())
			} else {
				pPlayer.sendSystemMessage(
						Component.literal("Flag: ") + (ourEnt.flagName.asComponent()).withStyle(ChatFormatting.BLUE)
				)
			}
		}.sidedResult
	}
	//endregion
}

class BlockFlagReader(props: Properties) : AbstractFlagHolderBlock(props), IPlatformConnectRedstone {
	companion object : IObjectData {
		override val id: String
			get() = "flag_reader"
		
		private const val TXT_SIDES = "logic_programmer_side"
		private const val TXT_SIDES_ON = "logic_programmer_side_on"
		private const val TXT_TOP = "logic_programmer_top"
		private const val TXT_TOP_ON = "logic_programmer_top_on"
//
//		override fun BlockStateProvider.buildModelsAndStates() {
//			val off = models().cubeColumn(id, blockLoc(TXT_SIDES), blockLoc(TXT_TOP))
//			val on = models().cubeColumn("${id}_on", blockLoc(TXT_SIDES_ON), blockLoc(TXT_TOP_ON))
//
//			variantDsl(ModBlocks.FLAG_READER) {
//				POWERED {
//					false {
//						model {
//							modelFile(off)
//						}
//					}
//					true {
//						model {
//							modelFile(on)
//						}
//					}
//				}
//			}
//
//			simpleBlockItem(ModBlocks.FLAG_READER, off)
//		}
//
	}
	
	
	override fun canConnectRedstone(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction?): Boolean = true
	
	override fun getSignal(pState: BlockState, pLevel: BlockGetter, pPos: BlockPos, pDirection: Direction): Int {
		if (pState.getValue(POWERED)) {
			return 15
		} else {
			return 0
		}
	}
	
	override fun <T : BlockEntity> getTicker(pLevel: Level, pState: BlockState, pBlockEntityType: BlockEntityType<T>): BlockEntityTicker<T>? {
		if (pLevel.isClientSide)
			return null
		
		// This is laggy as hell, but this only works this way for the builder and not for the compiled dungeon.
		// We'll see if it's really bad when people are using the builder or if it's at least bearable.
		return BlockEntityTicker { level, pos, state, ent ->
			if (level !is ServerLevel || ent !is TileFlagHolder)
				return@BlockEntityTicker
			
			val isPowered = state.getValue(POWERED)
			val ourFlag = ent.flagName ?: run {
				if (isPowered) {
					level.setBlockAndUpdate(pos, state.with(POWERED, false))
				}
				return@BlockEntityTicker
			}
			
			val shouldBePowered = level.dataStorage.dungeonBuilderData.getFlag(ourFlag)
			if (isPowered != shouldBePowered) {
				level.setBlockAndUpdate(pos, state.with(POWERED, !isPowered))
			}
		}
	}
}


class BlockFlagWriter(props: Properties, /** True = is a "setter", false = is a "resetter" */ val isSetter: Boolean) : AbstractFlagHolderBlock(props), IPlatformConnectRedstone {
	companion object : IObjectData {
		val FACING = BlockStateProperties.HORIZONTAL_FACING
		
		override val id: String
			get() = "flag_writer"
//
//		//region DataGen
//		// TODO: Replace dev textures with something we actually own
//		private const val TEXTURE_SIDES = "logic_programmer_side"
//		private const val TEXTURE_ON_SIDES = "logic_programmer_side_on"
//
//		private const val TEXTURE_SIDES_INPUT = "logic_programmer_side_input"
//		private const val TEXTURE_ON_SIDES_INPUT = "logic_programmer_side_input_on"
//
//		private const val TXT_SETTER_TOP = "logic_programmer_top_setter"
//		private const val TXT_SETTER_TOP_ON = "logic_programmer_top_on_setter"
//
//		private const val TXT_RESETTER_TOP = "logic_programmer_top_resetter"
//		private const val TXT_RESETTER_TOP_ON = "logic_programmer_top_on_resetter"
//
//		@Suppress("DuplicatedCode")
//		override fun BlockStateProvider.buildModelsAndStates() {
//			this.forSetter()
//			this.forResetter()
//		}
//
//		@Suppress("DuplicatedCode")
//		private fun BlockStateProvider.forSetter() {
//			val tx_caps_off = blockLoc(TXT_SETTER_TOP)
//			val tx_caps_on = blockLoc(TXT_SETTER_TOP_ON)
//			val tx_side_off = blockLoc(TEXTURE_SIDES)
//			val tx_side_on = blockLoc(TEXTURE_ON_SIDES)
//			val tx_side_off_input = blockLoc(TEXTURE_SIDES_INPUT)
//			val tx_side_on_input = blockLoc(TEXTURE_ON_SIDES_INPUT)
//
//			val setter_off = models().cube("flag_setter", tx_caps_off, tx_caps_off, tx_side_off_input, tx_side_off, tx_side_off, tx_side_off)
//			val setter_on = models().cube("flag_setter_on", tx_caps_on, tx_caps_on, tx_side_on_input, tx_side_on, tx_side_on, tx_side_on)
//
//			variantDsl(ModBlocks.FLAG_SETTER) {
//				POWERED {
//					false {
//						FACING {
//							rotateForEachHorizontal(setter_off)
//						}
//					}
//					true {
//						FACING {
//							rotateForEachHorizontal(setter_on)
//						}
//					}
//				}
//			}
//
//			simpleBlockItem(ModBlocks.FLAG_SETTER, setter_off)
//		}
//
//		@Suppress("DuplicatedCode")
//		private fun BlockStateProvider.forResetter() {
//			val tx_caps_off = blockLoc(TXT_RESETTER_TOP)
//			val tx_caps_on = blockLoc(TXT_RESETTER_TOP_ON)
//			val tx_side_off = blockLoc(TEXTURE_SIDES)
//			val tx_side_on = blockLoc(TEXTURE_ON_SIDES)
//			val tx_side_off_input = blockLoc(TEXTURE_SIDES_INPUT)
//			val tx_side_on_input = blockLoc(TEXTURE_ON_SIDES_INPUT)
//
//			val off = models().cube("flag_resetter", tx_caps_off, tx_caps_off, tx_side_off_input, tx_side_off, tx_side_off, tx_side_off)
//			val on = models().cube("flag_resetter_on", tx_caps_on, tx_caps_on, tx_side_on_input, tx_side_on, tx_side_on, tx_side_on)
//
//			variantDsl(ModBlocks.FLAG_RESETTER) {
//				POWERED {
//					false {
//						FACING {
//							rotateForEachHorizontal(off)
//						}
//					}
//					true {
//						FACING {
//							rotateForEachHorizontal(on)
//						}
//					}
//				}
//			}
//
//			simpleBlockItem(ModBlocks.FLAG_RESETTER, off)
//		}
//		//endregion
//
	}
	
	init {
		registerDefaultState(stateDefinition.any().with(FACING, Direction.NORTH).with(POWERED, false))
	}
	
	override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
		super.createBlockStateDefinition(pBuilder)
		pBuilder.add(FACING)
	}
	
	override fun tick(pState: BlockState, pLevel: ServerLevel, pPos: BlockPos, pRandom: RandomSource) {
		super.tick(pState, pLevel, pPos, pRandom)
		
//		checkShouldSetFlag(pLevel, pPos, pState)
	}
	
	override fun neighborChanged(pState: BlockState, pLevel: Level, pPos: BlockPos, pNeighborBlock: Block, orientation: Orientation?, pMovedByPiston: Boolean) {
		super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, orientation, pMovedByPiston)
		if (pLevel !is ServerLevel)
			return;
		
		checkShouldSetFlag(pLevel, pPos, pState)
	}
	
	private fun checkShouldSetFlag(pLevel: ServerLevel, pNeighborPos: BlockPos, pState: BlockState) {
		val pDirection = pState.getValue(FACING)
		
		if (!pLevel.hasSignal(pNeighborPos, pDirection))
			return;
		
		val ourFlag = pLevel.blockEntity(pNeighborPos, ModBlocks.FLAG_BLOCK_ENTITY)!!.flagName ?: return
		
		val newvalue = isSetter
		
		pLevel.dataStorage.dungeonBuilderData.setFlag(ourFlag, newvalue)
	}
	
	override fun canConnectRedstone(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction?): Boolean {
		return direction == state.getValue(FACING).opposite
	}
	
	override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? {
		return defaultBlockState().with(FACING, pContext.horizontalDirection.opposite)
	}
}


class TileFlagHolder(pPos: BlockPos, pState: BlockState) : BlockEntity(ModBlocks.FLAG_BLOCK_ENTITY, pPos, pState)
{
	class State(
		pFlagName: FlagName? = null,
		override var onChange: () -> Unit = {},
		pAddFlagFunc: (String) -> Unit = {}
	) : IOnChange, ICodecSerializable<State> {
		override fun codec() = CODEC
		override fun copyFrom(other: State) {
			this.flagName.value = other.flagName.value
		}
		
		companion object {
			val CODEC = RecordCodecBuilder.create { inst ->
				inst.group(
						Codec.STRING.nullableFieldOf("flag_name") { it: State -> it.flagName.value }
				).apply(inst, { pFlagName-> State(pFlagName.getOrNull()) })
			}
		}
		
		val flagName = object : IItemRepresentable<String> {
			override var value: String? = pFlagName
				set(value) {
					field = value
					// Add this flag to the world data.
					// I don't know if we might need to delete the flag once no one's watching it anymore, but that's something to consider.
					if (value != null)
						pAddFlagFunc(value)
					
					onChange()
				}
			
			override fun writeToItem(): ItemStack? {
				if (value == null)
					return null
				
				return ModItems.FLAG_ITEM.stack().apply {
					setDisplayName(this, value!!)
				}
			}
			
			override fun setFromItem(stack: ItemStack): Boolean {
				TODO("Not yet implemented")
			}
			
			override fun acceptsItem(stack: ItemStack): Boolean {
				return stack.`is`(ModItems.FLAG_ITEM)
			}
			
		}
	}
	
	companion object {
		const val TAGKEY_STATE = "state"
	}
	
	private val state: State = State(onChange=this::setChanged, pAddFlagFunc={ (this.level as? ServerLevel)?.dataStorage?.dungeonBuilderData?.addFlag(it) })
	
	val flagName: String?
		get() = state.flagName.value
	var flagItem: ItemStack
		get() = state.flagName.asItem
		set(value) {
			state.flagName.asItem = value
		}
	
	
	override fun saveAdditional(output: ValueOutput) {
		super.saveAdditional(output)
		output.putCodecSerializable(TAGKEY_STATE, state)
	}
	
	override fun loadAdditional(input: ValueInput) {
		super.loadAdditional(input)
		input.readCodecSerializableToExisting(TAGKEY_STATE, state)
	}
	
	fun hasFlag(): Boolean {
		return this.flagName != null
	}
}