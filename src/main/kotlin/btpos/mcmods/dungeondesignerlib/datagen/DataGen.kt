package btpos.mcmods.dungeondesignerlib.datagen

import btpos.mcmods.dungeondesignerlib.MODID
import btpos.mcmods.dungeondesignerlib.builder.blocks.BlockDungeonNexus
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.BlockFightController
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.BlockFlagReader
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.BlockFlagWriter
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.BlockTriggerHolder
import btpos.mcmods.dungeondesignerlib.builder.items.ItemEntityPipette
import btpos.mcmods.dungeondesignerlib.builder.items.ItemFlagVariable
import btpos.mcmods.dungeondesignerlib.builder.items.ItemTriggerVariable
import net.minecraft.data.PackOutput
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.data.event.GatherDataEvent

object DataGenConstants {
	fun gatherDataEvent(evt: GatherDataEvent) {
		val efh = evt.existingFileHelper
		
		evt.generator.apply {
			addProvider<BlockStateGen>(evt.includeClient(), {output -> BlockStateGen(output, efh)})
			addProvider<ItemDataGen>(evt.includeClient(), { output -> ItemDataGen(output, efh)})
		}
	}
}

class BlockStateGen(output: PackOutput, efh: ExistingFileHelper) : BlockStateProvider(output, MODID, efh) {
	override fun registerStatesAndModels() {
		listOf(
			BlockDungeonNexus,
			BlockTriggerHolder,
			BlockFlagReader,
			BlockFlagWriter,
			BlockFightController
		).forEach {
			it.getStatesAndModels(this)
		}
	}
}

class ItemDataGen(output: PackOutput, efh: ExistingFileHelper) : ItemModelProvider(output, MODID, efh) {
	override fun registerModels() {
		listOf(
			ItemTriggerVariable,
			ItemFlagVariable,
			ItemEntityPipette
		).forEach {
			it.getModels(this)
		}
	}
}