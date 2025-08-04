package btpos.mcmods.dungeondesigner

import btpos.mcmods.dungeondesigner.MultiPlatformHooksHooks.getPlatformSpecificStuff
import btpos.mcmods.dungeondesigner.multiplatform.capabilities.IItemHandler
import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

/**
 * Poor man's `expect object` `actual object` :/
 *
 * God why did KMP have to stop supporting multi-JVM targets
 */
object MultiplatformHooks : IPlatformSpecificStuff by getPlatformSpecificStuff()

object MultiPlatformHooksHooks {
	@JvmStatic @ExpectPlatform
	fun getPlatformSpecificStuff() : IPlatformSpecificStuff {
		throw IllegalStateException("Transformer not merged")
	}
}

interface IPlatformSpecificStuff {
	fun getItemHandler(level: Level, pos: BlockPos, direction: Direction?): IItemHandler?
	
	fun BlockEntity.getItemHandler(direction: Direction?): IItemHandler?
}


