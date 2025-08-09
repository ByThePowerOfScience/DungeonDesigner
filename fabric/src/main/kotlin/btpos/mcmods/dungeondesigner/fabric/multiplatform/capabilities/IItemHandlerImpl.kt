package btpos.mcmods.dungeondesigner.fabric.multiplatform.capabilities

import btpos.mcmods.dungeondesigner.multiplatform.capabilities.IItemHandler
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.world.item.ItemStack

@JvmInline
value class IItemHandlerImpl(val storage: Storage<ItemVariant>) : IItemHandler {
	override fun iterSlots(): Sequence<ItemStack> {
		return storage.nonEmptyIterator().asSequence().map { it.resource.toStack(it.amount.toInt()) }
	}
}