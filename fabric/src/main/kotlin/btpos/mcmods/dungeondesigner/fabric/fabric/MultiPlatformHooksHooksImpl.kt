package btpos.mcmods.dungeondesigner.fabric.fabric

import btpos.mcmods.dungeondesigner.IPlatformSpecificStuff
import btpos.mcmods.dungeondesigner.fabric.fabric.capabilities.IItemHandlerImpl
import btpos.mcmods.dungeondesigner.multiplatform.capabilities.IItemHandler
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

@Suppress("unused")
object MultiPlatformHooksHooksImpl {
	@JvmStatic
	fun getPlatformSpecificStuff() : IPlatformSpecificStuff {
		return PlatformSpecificStuffFabric
	}
}

object PlatformSpecificStuffFabric : IPlatformSpecificStuff {
	val ITEM_LOOKUP: BlockApiLookup<Storage<ItemVariant>, Direction?> = ItemStorage.SIDED
	override fun getItemHandler(level: Level, pos: BlockPos, direction: Direction?): IItemHandler? {
		return ITEM_LOOKUP.find(level, pos, direction)?.let(::IItemHandlerImpl)
	}
	
	override fun BlockEntity.getItemHandler(direction: Direction?): IItemHandler? {
		return ITEM_LOOKUP.find(level, blockPos, blockState, this, direction)?.let(::IItemHandlerImpl)
	}
}