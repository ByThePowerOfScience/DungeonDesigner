package btpos.mcmods.dungeondesignerlib.blocks

import btpos.mcmods.devutil.forge.datagen.IBlockDataGen
import btpos.mcmods.dungeondesignerlib.registry.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.client.model.generators.BlockModelProvider
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ItemModelProvider

class BlockDungeonNexus(props: Properties) : Block(props), EntityBlock {
	override fun newBlockEntity(p0: BlockPos, p1: BlockState): BlockEntity? = ModBlocks.DUNGEON_NEXUS_ENTITY.create(p0, p1)
	
	companion object DataGen : IBlockDataGen {
		override val id: String
			get() = ID

		const val ID = "dungeon_nexus"

		const val TEXTURE = "blocks/$ID"

		override fun BlockModelProvider.getModels() {
			cubeAll(id, modLoc(TEXTURE))
		}

		override fun BlockStateProvider.getStates() {
			cubeAll(ModBlocks.DUNGEON_NEXUS)
		}
		
		override fun ItemModelProvider.getItems() {
			this.basicItem(ModBlocks.DUNGEON_NEXUS_ITEM)
		}
	}
}

class TileDungeonNexus(pos: BlockPos, state: BlockState) : BlockEntity(ModBlocks.DUNGEON_NEXUS_ENTITY, pos, state) {

}