package btpos.mcmods.dungeondesigner.multiplatform.capabilities

import btpos.mcmods.devutil.common.ext.vanilla.isNotEmpty
import net.minecraft.world.item.ItemStack

interface IItemHandler {
	val numSlots: Int
	
	fun getStackInSlot(slot: Int): ItemStack
	
	fun iterSlots(): Sequence<ItemStack> {
		return (0..<this.numSlots).asSequence().map(this::getStackInSlot)
	}
	
	fun iterFullSlots(): Sequence<ItemStack> {
		return iterSlots().filter(ItemStack::isNotEmpty)
	}
}