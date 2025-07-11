package btpos.mcmods.devutil.forge.datagen

import net.minecraftforge.client.model.generators.BlockModelProvider
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ItemModelProvider

interface IBlockDataGen : IObjectData {
	fun getStatesAndModels(provider: BlockStateProvider) {
		provider.models().getModels()
		provider.getStates()
		provider.itemModels().getItems()
	}
	
	fun BlockModelProvider.getModels()
	
	fun BlockStateProvider.getStates()
	
	fun ItemModelProvider.getItems()
}