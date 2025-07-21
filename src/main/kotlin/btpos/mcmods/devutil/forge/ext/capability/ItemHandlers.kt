package btpos.mcmods.devutil.forge.ext.capability

import btpos.mcmods.devutil.common.ext.vanilla.isNotEmpty
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler

fun IItemHandler.iterSlots(): Sequence<ItemStack> {
    return (0..<this.slots).asSequence().map(this::getStackInSlot)
}

fun IItemHandler.iterFullSlots(): Sequence<ItemStack> {
    return iterSlots().filter(ItemStack::isNotEmpty)
}