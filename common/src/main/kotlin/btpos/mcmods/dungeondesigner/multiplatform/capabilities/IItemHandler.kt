package btpos.mcmods.dungeondesigner.multiplatform.capabilities

import btpos.mcmods.devutil.common.ext.vanilla.isNotEmpty
import net.minecraft.world.item.ItemStack

interface IItemHandler {
	fun iterSlots(): Sequence<ItemStack>
	
	fun iterFullSlots(): Sequence<ItemStack> {
		return iterSlots().filter(ItemStack::isNotEmpty)
	}
}