package btpos.mcmods.dungeondesigner.neoforge

import btpos.mcmods.dungeondesigner.IPlatformSpecificStuff
import btpos.mcmods.dungeondesigner.multiplatform.capabilities.IItemHandler
import btpos.mcmods.dungeondesigner.neoforge.multiplatform.capabilities.IItemHandlerImpl
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.Capabilities

@Suppress("unused")
object MultiPlatformHooksHooksImpl {
	@JvmStatic
	fun getPlatformSpecificStuff() : IPlatformSpecificStuff {
		return PlatformSpecificStuffForge
	}
}

object PlatformSpecificStuffForge : IPlatformSpecificStuff {
	override fun getItemHandler(level: Level, pos: BlockPos, direction: Direction?): IItemHandler? {
		@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") // the compiler is wrong here
		return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, direction)?.let(::IItemHandlerImpl)
	}
	
	override fun BlockEntity.getItemHandler(direction: Direction?): IItemHandler? {
		@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
		return this.level?.getCapability(Capabilities.ItemHandler.BLOCK, this.blockPos, this.blockState, this, direction)?.let(::IItemHandlerImpl)
	}
}