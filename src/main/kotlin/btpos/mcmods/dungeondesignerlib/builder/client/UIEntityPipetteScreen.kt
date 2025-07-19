package btpos.mcmods.dungeondesignerlib.builder.client

import btpos.mcmods.devutil.parts.IItemRepresentable
import btpos.mcmods.devutil.parts.IItemRepresentable_Tag
import btpos.mcmods.dungeondesignerlib.builder.nbt.IMobData
import btpos.mcmods.dungeondesignerlib.builder.nbt.IPipetteData
import btpos.mcmods.dungeondesignerlib.builder.nbt.PipetteData
import btpos.mcmods.dungeondesignerlib.registry.ModItems
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SpawnEggItem

class UIEntityPipetteScreen(pMenu: UIEntityPipetteMenu, pPlayerInventory: Inventory, pTitle: Component) : AbstractContainerScreen<UIEntityPipetteMenu>(pMenu, pPlayerInventory, pTitle) {
	override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
		TODO("Not yet implemented")
	}
}
private const val INV_SLOT_START = 3
private const val INV_SLOT_END = 30
private const val USE_ROW_SLOT_START = 30
private const val USE_ROW_SLOT_END = 39
class UIEntityPipetteMenu(
	pMenuType: MenuType<*>?,
	pContainerId: Int,
	pPlayerInventory: Inventory,
	val pipetteStack: ItemStack
) : AbstractContainerMenu(pMenuType, pContainerId) {
	
	private fun addPlayerInventory(inventory: Inventory) {
		for (i in 0..2) {
			for (j in 0..8) {
				this.addSlot(Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
			}
		}
	}
	
	private fun addPlayerHotbar(inventory: Inventory) {
		for (k in 0..8) {
			this.addSlot(Slot(inventory, k, 8 + k * 18, 142))
		}
	}
	
	override fun quickMoveStack(pPlayer: Player, pIndex: Int): ItemStack {
		TODO("Not yet implemented")
	}
	
	override fun stillValid(pPlayer: Player): Boolean {
		return pPlayer.mainHandItem == pipetteStack || pPlayer.offhandItem == pipetteStack
	}
}

class PipetteSlot(pContainer: Container, pSlot: Int, pX: Int, pY: Int)
	: SlotOptic(
		object : IItemRepresentable_Tag<IMobData> {
			override val defaultItem: Item
				get() = ModItems.PIPETTE_ITEM
			
			override fun CompoundTag.readFromTag(): IPipetteData? {
			
			}
			
			override fun CompoundTag.writeToTag(value: IPipetteData) {
				TODO("Not yet implemented")
			}
			
			override var value: IPipetteData?
				get() = TODO("Not yet implemented")
				set(value) {}
			
		},
		pContainer, pSlot, pX, pY
	)
{

}

/**
 * A passthrough to the data of something arbitrary instead of a container.
 * Copied somewhat from EnderIO.
 */
class SlotOptic<T : Any>(
	val itemRepresentable: IItemRepresentable<T>,
	pSlot: Int, pX: Int, pY: Int
) : Slot(EMPTY_INVENTORY, pSlot, pX, pY) {
	companion object {
		val EMPTY_INVENTORY = SimpleContainer(0)
	}
	
	override fun getItem(): ItemStack {
		return itemRepresentable.asItem
	}
	
	override fun getMaxStackSize(): Int {
		return item.maxStackSize
	}
	
	override fun set(pStack: ItemStack) {
		itemRepresentable.asItem = pStack
	}
	
	override fun mayPlace(pStack: ItemStack): Boolean {
		return itemRepresentable.acceptsItem(pStack)
	}
	
	override fun safeInsert(pStack: ItemStack, increment: Int): ItemStack {
		if (!pStack.isEmpty && mayPlace(pStack)) {
			itemRepresentable.asItem = pStack
		}
		return pStack
	}
}
