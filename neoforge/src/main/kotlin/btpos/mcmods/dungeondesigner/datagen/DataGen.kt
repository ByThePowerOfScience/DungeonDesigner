package btpos.mcmods.dungeondesigner.datagen

import btpos.mcmods.dungeondesigner.MODID
import btpos.mcmods.dungeondesigner.builder.blocks.BlockDungeonNexus
import btpos.mcmods.dungeondesigner.builder.blocks.actors.BlockFightController
import btpos.mcmods.dungeondesigner.builder.blocks.actors.BlockFlagReader
import btpos.mcmods.dungeondesigner.builder.blocks.actors.BlockFlagWriter
import btpos.mcmods.dungeondesigner.builder.blocks.actors.BlockTriggerHolder
import btpos.mcmods.dungeondesigner.builder.items.ItemEntityPipette
import btpos.mcmods.dungeondesigner.builder.items.ItemFlagVariable
import btpos.mcmods.dungeondesigner.builder.items.ItemTriggerVariable
import btpos.mcmods.dungeondesigner.builder.redstone.blocks.BlockRedstoneReceiver
import btpos.mcmods.dungeondesigner.builder.redstone.blocks.BlockRedstoneTransmitter
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.data.event.GatherDataEvent

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
			BlockFightController,
			BlockRedstoneReceiver,
			BlockRedstoneTransmitter
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