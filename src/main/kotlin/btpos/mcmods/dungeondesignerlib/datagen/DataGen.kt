package btpos.mcmods.dungeondesignerlib.datagen

import btpos.mcmods.dungeondesignerlib.MODID
import net.minecraft.data.PackOutput
import net.minecraftforge.client.model.generators.BlockModelProvider
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.common.data.ExistingFileHelper
import kotlin.io.path.Path
private object foo {
	val output = PackOutput(Path(""))
}

class BlockStateGen(efh: ExistingFileHelper) : BlockStateProvider(foo.output, MODID, efh) {
	
	override fun registerStatesAndModels() {
	
	}
}

class BlockGen(efh: ExistingFileHelper) : BlockModelProvider(foo.output, MODID, efh) {
	override fun registerModels() {
	
	}
}