@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package btpos.mcmods.dungeondesignerlib.builder.blocks.actors

import btpos.mcmods.devutil.common.ext.kotlin.filterSplit
import btpos.mcmods.devutil.common.ext.kotlin.ifNull
import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.ext.vanilla.world.modifyBlockAndUpdate
import btpos.mcmods.devutil.common.ext.vanilla.world.get
import btpos.mcmods.devutil.common.ext.vanilla.world.with
import btpos.mcmods.devutil.common.util.BlockWithEntity
import btpos.mcmods.devutil.forge.datagen.IBlockDataGen
import btpos.mcmods.devutil.forge.datagen.rotateForEachHorizontal
import btpos.mcmods.devutil.forge.datagen.variantDsl
import btpos.mcmods.devutil.forge.ext.capability.getOrNull
import btpos.mcmods.devutil.forge.ext.capability.iterFullSlots
import btpos.mcmods.devutil.forge.ext.capability.iterSlots
import btpos.mcmods.dungeondesignerlib.builder.items.ItemEntityPipette
import btpos.mcmods.dungeondesignerlib.builder.nbt.IEntitySpawnData
import btpos.mcmods.dungeondesignerlib.builder.nbt.trySpawnEntity
import btpos.mcmods.dungeondesignerlib.registry.ModBlocks
import btpos.mcmods.dungeondesignerlib.registry.ModItems
import btpos.mcmods.dungeondesignerlib.MOD_LOGGER as DLOGGER
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.common.capabilities.ForgeCapabilities
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID
import kotlin.math.roundToInt


enum class FightStatus : StringRepresentable {
	INACTIVE,
	IN_PROGRESS,
	COMPLETE;
	
	override fun getSerializedName(): String = name.lowercase()
}

/**
 * Put into entities' persistentdata to say they shouldn't be saved to the chunk.
 * @see btpos.mcmods.dungeondesignerlib.mixin.MArenaDespawnOnUnload
 */
const val TAGKEY_SPAWNED_BY_FIGHT_CONTROLLER = "dungeondesigner_temporary"


class BlockFightController(props: Properties) : Block(props), BlockWithEntity<TileFightController> {
	companion object : IBlockDataGen {
		override val id: String
			get() = "fight_controller"
		
		override fun BlockStateProvider.buildModelsAndStates() {
			val t_off = blockLoc("fight_controller/${id}_inactive")
			val t_ip = blockLoc("fight_controller/${id}_ip")
			val t_com = blockLoc("fight_controller/${id}_complete")
			val out_off = blockLoc("fight_controller/${id}_inactive_out")
			val out_ip = blockLoc("fight_controller/${id}_ip_out")
			val out_com = blockLoc("fight_controller/${id}_complete_out")
			val off = models().cube(id + "_inactive", t_off, t_off, out_off, t_off, t_off, t_off)
			val com = models().cube(id + "_complete", t_com, t_com, out_com, t_com, t_com, t_com)
			val in_progress = models().cube(id + "_ip", t_ip, t_ip, out_ip, t_ip, t_ip, t_ip)
			
			variantDsl(ModBlocks.FIGHT_CONTROLLER) {
				STATUS {
					FightStatus.INACTIVE {
						FACING {
							rotateForEachHorizontal(off)
						}
					}
					FightStatus.IN_PROGRESS {
						FACING {
							rotateForEachHorizontal(in_progress)
						}
					}
					FightStatus.COMPLETE {
						FACING {
							rotateForEachHorizontal(com)
						}
					}
				}
			}
			
			simpleBlockItem(ModBlocks.FIGHT_CONTROLLER, models().cubeAll(id, t_off))
		}
		
		val STATUS = EnumProperty.create("status", FightStatus::class.java)
		
		/**
		 * Side to output redstone signal when the fight is complete.
		 */
		val FACING = BlockStateProperties.HORIZONTAL_FACING
	}
	
	init {
		registerDefaultState(stateDefinition.any().with(STATUS, FightStatus.INACTIVE).with(FACING, Direction.NORTH))
	}
	
	//region Setup
	override fun getEntityType() = ModBlocks.FIGHT_CONTROLLER_ENTITY
	
	override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block?, BlockState?>) {
		super.createBlockStateDefinition(pBuilder)
		pBuilder.add(STATUS, FACING)
	}
	//endregion
    
    
    //region Redstone Emitting
    override fun canConnectRedstone(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction?) = true
	
	override fun getSignal(pState: BlockState, pLevel: BlockGetter, pPos: BlockPos, pDirection: Direction): Int {
		if (pState[STATUS] == FightStatus.COMPLETE && pDirection == pState[FACING].opposite) {
			return 15
		} else {
			return 0
		}
	}
	
	override fun getAnalogOutputSignal(pState: BlockState, pLevel: Level, pPos: BlockPos): Int {
		val ourEnt = pLevel.getOurEntity(pPos) ?: return run { DLOGGER.warn("Fight Controller: null block entity at {}", pPos); 0 }
		
		val percentageMobsAlive = ourEnt.activeFight?.run {
			mobsAlive.size.toFloat() / totalMobs
		} ?: return 0
		
		return (percentageMobsAlive * 15).roundToInt().coerceAtMost(15)
	}
    //endregion
	
	override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? {
		return defaultBlockState().with(FACING, pContext.horizontalDirection.opposite)
	}
	
	// region Redstone Reception
	override fun onNeighborChange(state: BlockState, level: LevelReader, pos: BlockPos, neighbor: BlockPos) {
		super.onNeighborChange(state, level, pos, neighbor)
		
		checkShouldStartFight(state, level, pos)
	}
	
	override fun neighborChanged(
		pState: BlockState,
		pLevel: Level,
		pPos: BlockPos,
		pNeighborBlock: Block,
		pNeighborPos: BlockPos,
		pMovedByPiston: Boolean
	) {
		super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston)
		checkShouldStartFight(pState, pLevel, pPos)
	}
	
	private fun checkShouldStartFight(
		state: BlockState,
		level: LevelReader,
		pos: BlockPos
	) {
		if (state[STATUS] != FightStatus.INACTIVE)
			return
		
		val facing = state[FACING]
		
		val shouldStartFight = Direction.Plane.HORIZONTAL.any {
			it != facing && level.hasSignal(pos, it)
		}
		
		if (shouldStartFight) {
			level.getOurEntity(pos)?.startFight()
		}
	}
	// endregion
	
	
	/**
	 * This is called every time the blockstate changes.
	 */
	override fun <T : BlockEntity> getTicker(
		pLevel: Level,
		pState: BlockState,
		pBlockEntityType: BlockEntityType<T>
	): BlockEntityTicker<T>? {
		if (pState[STATUS] != FightStatus.IN_PROGRESS)
			return null;
		return BlockEntityTicker { level, pos, state, t ->
			(t as? TileFightController)?.onTick()
		}
	}
}

/**
 * Fight progress is reset every time the chunk is unloaded, and the mobs are prevented from being saved to the chunk with a Mixin.
 * @see btpos.mcmods.dungeondesignerlib.mixin.MArenaDespawnOnUnload
 */
class TileFightController(pPos: BlockPos, pState: BlockState)
	: BlockEntity(ModBlocks.FIGHT_CONTROLLER_ENTITY, pPos, pState)
{
	// Reset the fight status on load, which is the equivalent of resetting it when the chunk is unloaded
	fun resetFightStateOnLoad() {
		if (blockState[BlockFightController.STATUS] != FightStatus.INACTIVE)
			level?.setBlockAndUpdate(blockPos, blockState.with(BlockFightController.STATUS, FightStatus.INACTIVE))
	}
	
	override fun onLoad() {
		super.onLoad()
		resetFightStateOnLoad()
	}
	/**
	 * Keeps track of spawned entities.
	 * Not synced to NBT.
	 */
	data class ActiveFightState(
		val mobsAlive: MutableList<UUID>
	) {
		/**
		 * How many mobs we started with. For calculating comparator output.
		 */
		val totalMobs: Int = mobsAlive.size
	}
	
	/**
	 * When a fight is in progress, holds the mobs this fight controller currently has spawned in the world. When no fight is active, is `null`.
	 */
	var activeFight: ActiveFightState? = null
	
	
	fun startFight() {
		val level = this.level as? ServerLevel ?: return
		
		val mobPipettes: List<IEntitySpawnData> = getPipetteData() ?: return
		
		val (matching, notMatching) = mobPipettes.filterSplit { it.type != null && it.pos != null }
		
		notMatching.forEach {
			val msg = when (null) {
                it.type -> "No mob found"
                it.pos -> "No pos selected for mob ${it.type}"
                it.rotation -> "No spawn rotation for mob ${it.type}"
				else -> "Unknown error for mob ${it.type}"
            }
			
			printError("$msg. Skipping.".asComponent())
		}
		
		val mobsAlive = matching.mapNotNullTo(ArrayList(matching.size)) { data ->
			data.trySpawnEntity(level).ifNull { printError("Failed to spawn entity.".asComponent()) }
		}
		
		activeFight = ActiveFightState(mobsAlive)
		
		level.setBlockAndUpdate(this.blockPos, this.blockState.with(BlockFightController.STATUS, FightStatus.IN_PROGRESS))
	}
	
	/**
	 * Check if its mobs exist and remove them from the list if they don't.
	 */
	fun onTick() {
		// Only check every quarter-second
		val ticksElapsed = level?.server?.tickCount ?: return
		if (ticksElapsed % 5 != 0)
			return;
		
		val activeFight = activeFight ?: return
		val level = level as? ServerLevel ?: return
		
		activeFight.mobsAlive.removeAll { id ->
			val ent = level.getEntity(id)
			ent == null || ent.isRemoved
		}
		
		if (activeFight.mobsAlive.isEmpty()) {
			// End the fight
			this.activeFight = null
			level.modifyBlockAndUpdate(this.blockPos) { it.with(BlockFightController.STATUS, FightStatus.COMPLETE) }
		 }
	}
	
	val isFightInProgress: Boolean
		get() = this.activeFight != null
	
	/**
	 * Get pipettes from the chest above the block
	 */
	@VisibleForTesting
	fun getPipetteData(): List<IEntitySpawnData>? {
		val entAbove = (level as? ServerLevel)?.getBlockEntity(this.blockPos.above()) ?: return null
		val itemCap = entAbove.getCapability(ForgeCapabilities.ITEM_HANDLER, null).getOrNull() ?: return null
		println("Count: " + itemCap.iterSlots().count())
		return itemCap.iterFullSlots().onEach{ println("Full slot: $it") }
			.filter { it.`is`(ModItems.PIPETTE_ITEM) }.onEach { println("Pipette: $it") }
			.mapNotNull(ItemEntityPipette::getDataOrNull).onEach { println("Has data: $it") }
			.filter(ItemEntityPipette::isReadyToSpawn)
			.toList()
	}
	
	private fun printError(msg: Component) {
		(level as? ServerLevel)?.players()?.forEach {
			it.sendSystemMessage(msg, true)
		}
	}
}

