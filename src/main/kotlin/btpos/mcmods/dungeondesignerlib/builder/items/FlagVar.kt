package btpos.mcmods.dungeondesignerlib.builder.items

import btpos.mcmods.devutil.common.ext.vanilla.data.getCompoundOrNull
import btpos.mcmods.devutil.common.ext.vanilla.data.getOrCreateCompound
import btpos.mcmods.devutil.common.ext.vanilla.data.getStringOrNull
import btpos.mcmods.devutil.forge.datagen.IItemDataGen
import btpos.mcmods.dungeondesignerlib.registry.ModItems
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentContents
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.model.generators.ItemModelProvider

class ItemFlagVariable(props: Properties) : Item(props) {
	companion object : IItemDataGen {
		override val id: String
			get() = "flag_variable"
		
		override fun ItemModelProvider.buildModels() {
			basicItem(ModItems.FLAG_ITEM)
		}
	}
	
	/**
	 * Managed access to the data we want from this ItemStack's NBT.
	 */
	@JvmInline
	value class NbtAdapter(val tag: CompoundTag) {
		/**
		 * Get the name of the flag from the actual anvil name
		 */
		var name: String?
			get() = tag.getCompoundOrNull(ItemStack.TAG_DISPLAY)?.getStringOrNull(ItemStack.TAG_DISPLAY_NAME)
			set(value) {
				if (value == null) {
					tag.getCompoundOrNull(ItemStack.TAG_DISPLAY)?.remove(ItemStack.TAG_DISPLAY_NAME)
					return;
				}
				else {
					tag.getOrCreateCompound(ItemStack.TAG_DISPLAY).putString(ItemStack.TAG_DISPLAY_NAME, value)
				}
			}
	}
}

