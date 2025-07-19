@file:Suppress("OVERRIDE_DEPRECATION")

package btpos.mcmods.dungeondesignerlib.builder.blocks.actors

import btpos.mcmods.devutil.common.ext.kotlin.filterSplit
import btpos.mcmods.devutil.common.ext.kotlin.ifNull
import btpos.mcmods.devutil.common.ext.kotlin.isNullOrTrue
import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.ext.vanilla.world.get
import btpos.mcmods.devutil.common.ext.vanilla.world.with
import btpos.mcmods.devutil.common.util.BlockWithEntity
import btpos.mcmods.devutil.forge.datagen.IBlockDataGen
import btpos.mcmods.devutil.forge.datagen.variantDsl
import btpos.mcmods.dungeondesignerlib.builder.nbt.IPipetteData
import btpos.mcmods.dungeondesignerlib.builder.nbt.PipetteData
import btpos.mcmods.dungeondesignerlib.registry.ModBlocks
import btpos.mcmods.dungeondesignerlib.LOGGER as DLOGGER
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraftforge.client.model.generators.BlockStateProvider
import java.lang.ref.WeakReference
import java.util.UUID


enum class FightStatus : StringRepresentable {
	INACTIVE,
	IN_PROGRESS,
	COMPLETE;
	
	override fun getSerializedName(): String = name
}


class BlockFightController(props: Properties) : Block(props), BlockWithEntity<TileFightController> {
	companion object : IBlockDataGen {
		override val id: String
			get() = "fight_controller"
		
		override fun BlockStateProvider.buildModelsAndStates() {
			val off = models().cubeAll(id + "_inactive", modLoc("fight_controller/${id}_inactive"))
			val in_progress = models().cubeAll(id + "_ip", modLoc("fight_controller/${id}_ip"))
			val finished = models().cubeAll(id + "_complete", modLoc("fight_controller/${id}_complete"))
			
			variantDsl(ModBlocks.FIGHT_CONTROLLER) {
				STATUS {
					FightStatus.INACTIVE {
						model {
							modelFile(off)
						}
					}
					FightStatus.IN_PROGRESS {
						model {
							modelFile(in_progress)
						}
					}
					FightStatus.COMPLETE {
						model {
							modelFile(finished)
						}
					}
				}
			}
			
			simpleBlockItem(ModBlocks.FIGHT_CONTROLLER, off)
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
	
	//region Configuration
	override fun getEntityType() = ModBlocks.FIGHT_CONTROLLER_ENTITY
	
	override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block?, BlockState?>) {
		super.createBlockStateDefinition(pBuilder)
	}
	//endregion
	
	
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
		val numMobsAlive = ourEnt.activeFight?.mobsAlive?.size ?: return 0
		val percentageMobsAlive = numMobsAlive.toFloat() / ourEnt.activeFight.totalMobs
		
		return (percentageMobsAlive * 15).toInt()
	}
}

/**
 * Unlike the compiled form, this one just has its entities stored in a
 */
class TileFightController(pPos: BlockPos, pState: BlockState)
	: BlockEntity(ModBlocks.FIGHT_CONTROLLER_ENTITY, pPos, pState)
{
	/**
	 * Not synced to NBT.
	 */
	data class ActiveFightState(
		var totalMobs: Int = 0,
		val mobsAlive: MutableList<UUID> = mutableListOf()
	)
	
	val activeFight: ActiveFightState? = null
	
	fun startFight() {
		val level = this.level as? ServerLevel ?: return
		
		val mobPipettes: List<IPipetteData> = getPipetteData()
		
		val (matching, notMatching) = mobPipettes.filterSplit { it.mob?.type == null || it.spawnPos == null }
		
		notMatching.forEach {
			val msg = if (it.mob == null) {
				"No mob found"
			} else if (it.spawnPos == null) {
				"No pos selected for mob ${it.mob}"
			} else if (it.mob?.type == null) {
				"Invalid mob"
			} else {
				"Unknown error"
			}
			
			printError("$msg. Skipping.".asComponent())
		}
		
		val mobsAlive = matching.mapNotNull { (mob, spawnPos) ->
			val (mobType, nbt) = mob!!
			return@mapNotNull mobType?.create(level, nbt, null, spawnPos!!, MobSpawnType.MOB_SUMMONED, true, false)
				.ifNull { printError("Null mob type".asComponent()) }
				?.uuid
		}
		
		val totalMobs = mobsAlive.size
		TODO("set state")
	}
	
	private fun getPipetteData(): List<IPipetteData> {
		TODO("Not yet implemented")
	}
	
	private fun printError(msg: Component) {
		(level as? ServerLevel)?.players()?.forEach {
			it.sendSystemMessage(msg, true)
		}
	}
	
	override fun onChunkUnloaded() {
		super.onChunkUnloaded()
		if (level is ServerLevel) {
			removeAllSpawnedMobs(level as ServerLevel)
		}
	}
	
	fun removeAllSpawnedMobs(level: ServerLevel) {
		activeFight?.mobsAlive?.forEach {
			level.entities.get(it)?.remove(Entity.RemovalReason.DISCARDED)
		}
	}
}

