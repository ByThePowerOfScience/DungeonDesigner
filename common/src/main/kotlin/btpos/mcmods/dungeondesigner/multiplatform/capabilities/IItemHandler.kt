package btpos.mcmods.dungeondesigner.multiplatform.capabilities

import btpos.mcmods.devutil.common.ext.vanilla.isNotEmpty
import net.minecraft.world.item.ItemStack

interface IItemHandler {
	/**
	 * Return a read-only view of the items in this container
	 */
	fun iterSlots(): Sequence<ItemStack>
	
	fun iterFullSlots(): Sequence<ItemStack> {
		return iterSlots().filter(ItemStack::isNotEmpty)
	}
}