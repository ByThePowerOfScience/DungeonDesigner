package btpos.mcmods.dungeondesigner.fabric.fabric.capabilities

import btpos.mcmods.dungeondesigner.multiplatform.capabilities.IItemHandler
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.world.item.ItemStack

@JvmInline
value class IItemHandlerImpl(val storage: Storage<ItemVariant>) : IItemHandler {
	
	override fun iterSlots(): Sequence<ItemStack> {
		TODO("Not yet implemented")
	}
}