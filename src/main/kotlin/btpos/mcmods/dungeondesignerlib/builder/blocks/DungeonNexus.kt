package btpos.mcmods.dungeondesignerlib.builder.blocks

import btpos.mcmods.devutil.forge.datagen.IBlockDataGen
import btpos.mcmods.dungeondesignerlib.registry.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ItemModelProvider

class BlockDungeonNexus(props: Properties) : Block(props), EntityBlock {
	override fun newBlockEntity(p0: BlockPos, p1: BlockState): BlockEntity? = ModBlocks.DUNGEON_NEXUS_ENTITY.create(p0, p1)
	
	companion object : IBlockDataGen {
		override val id get() = "dungeon_nexus"
		
		override fun BlockStateProvider.buildModelsAndStates() {
			simpleBlockWithItem(ModBlocks.DUNGEON_NEXUS, cubeAll(ModBlocks.DUNGEON_NEXUS))
		}
	}
}

class TileDungeonNexus(pos: BlockPos, state: BlockState) : BlockEntity(ModBlocks.DUNGEON_NEXUS_ENTITY, pos, state) {
	
	// TODO
	
	override fun saveAdditional(tag: CompoundTag) {
		super.saveAdditional(tag)
	}
}


/*
1. Make the tile
2. Refactor the state to be codec serializable
3. Refactor the items to work when the nexus is moved
 */

//class DungeonNexusInternalState(
//	/**
//	 * We use this so other items that target this block can do so even after it's been moved.
//	 * @see TODO world data map between uuid and blockpos
//	 */
//	val globalIdentifier: UUID = UUID.randomUUID(),
//
//	) : ICodecSerializable<DungeonNexusInternalState> {
//	override fun codec(): Codec<DungeonNexusInternalState> = CODEC
//	companion object {
//		val CODEC = codec {
//			group(
//					"uuid" - UUIDUtil.CODEC gets DungeonNexusInternalState::globalIdentifier
//			).apply(this, { DungeonNexusInternalState() })
//		}
//	}
//
//	override fun copyFrom(other: DungeonNexusInternalState) {
//		TODO("Not yet implemented")
//	}
//}





