package btpos.mcmods.dungeondesignerlib.builder.items

import btpos.mcmods.devutil.forge.datagen.IItemDataGen
import btpos.mcmods.dungeondesignerlib.registry.ModItems
import net.minecraft.world.item.Item
import net.minecraftforge.client.model.generators.ItemModelProvider

class ItemFlagVariable(props: Properties) : Item(props) {
	companion object : IItemDataGen {
		override val id: String
			get() = "flag_variable"
		
		override fun ItemModelProvider.buildModels() {
			basicItem(ModItems.FLAG_ITEM)
		}
	}
}

