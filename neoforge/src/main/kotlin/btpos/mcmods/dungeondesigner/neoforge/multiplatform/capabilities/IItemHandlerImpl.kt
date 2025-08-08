package btpos.mcmods.dungeondesigner.neoforge.multiplatform.capabilities

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler
import btpos.mcmods.dungeondesigner.multiplatform.capabilities.IItemHandler as DDHandler

@JvmInline
value class IItemHandlerImpl(val wrapped: IItemHandler) : DDHandler {
	override val numSlots: Int
		get() = wrapped.slots
	
	override fun getStackInSlot(slot: Int): ItemStack {
		return wrapped.getStackInSlot(slot)
	}
}