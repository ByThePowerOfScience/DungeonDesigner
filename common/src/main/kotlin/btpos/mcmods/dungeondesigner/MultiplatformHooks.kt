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
object MultiplatformHooks : IPlatformSpecificStuff {
	// perform manual delegation so IDEA actually gives me suggestions as though these were static methods
	// ...yes I know at this point it would be better to just use @JvmStatic @ExpectPlatform for all of these...
	// On the other hand, I'd need an interface anyway to make sure I didn't miss any
	private val delegate = getPlatformSpecificStuff()
	
	override fun getItemHandler(level: Level, pos: BlockPos, direction: Direction?): IItemHandler? {
		return delegate.getItemHandler(level, pos, direction)
	}
	
	override fun BlockEntity.getItemHandler(direction: Direction?): IItemHandler? {
		return with (delegate) { this@getItemHandler.getItemHandler(direction) }
	}
}

private object MultiPlatformHooksHooks {
	@JvmStatic @ExpectPlatform
	fun getPlatformSpecificStuff() : IPlatformSpecificStuff {
		throw IllegalStateException("Transformer not merged")
	}
}

interface IPlatformSpecificStuff {
	fun getItemHandler(level: Level, pos: BlockPos, direction: Direction?): IItemHandler?
	
	fun BlockEntity.getItemHandler(direction: Direction?): IItemHandler?
}


