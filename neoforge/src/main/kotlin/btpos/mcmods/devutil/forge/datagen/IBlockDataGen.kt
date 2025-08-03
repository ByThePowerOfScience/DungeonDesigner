@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package btpos.mcmods.devutil.forge.datagen

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.client.model.generators.ModelProvider

interface IBlockDataGen : IObjectData {
	fun getStatesAndModels(provider: BlockStateProvider) {
		provider.buildModelsAndStates()
	}
	
	fun BlockStateProvider.buildModelsAndStates()
	
	fun BlockStateProvider.blockLoc(path: String): ResourceLocation {
		return modLoc("${ModelProvider.BLOCK_FOLDER}/$path")
	}
	
	fun String.powered(isPowered: Boolean): String {
		return this + if (isPowered) "_powered" else "_unpowered"
	}
}

context(BlockStateProvider)
fun String.blockLoc(): ResourceLocation {
	return modLoc("${ModelProvider.BLOCK_FOLDER}/$this")
}

context(ItemModelProvider)
fun String.itemLoc(): ResourceLocation {
	return modLoc("${ModelProvider.ITEM_FOLDER}/$this")
}

interface IItemDataGen : IObjectData {
	fun getModels(provider: ItemModelProvider) {
		provider.buildModels()
	}
	
	fun ItemModelProvider.buildModels()
	
	fun ItemModelProvider.basicItem() {
		this.basicItem(modLoc(id))
	}
}