@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package btpos.mcmods.devutil.forge.datagen

import btpos.mcmods.devutil.common.structure.blocks.IObjectData
import net.minecraft.resources.ResourceLocation

typealias BlockStateProvider = Unit

interface IBlockDataGen : IObjectData {
	fun getStatesAndModels(provider: BlockStateProvider) {
		provider.buildModelsAndStates()
	}

	fun BlockStateProvider.buildModelsAndStates()
	

	fun String.powered(isPowered: Boolean): String {
		return this + if (isPowered) "_powered" else "_unpowered"
	}
}

context(IObjectData)
fun String.blockLoc(): ResourceLocation {
	return modLoc("block/$this")
}

context(IObjectData)
fun String.itemLoc(): ResourceLocation {
	return modLoc("item/$this")
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