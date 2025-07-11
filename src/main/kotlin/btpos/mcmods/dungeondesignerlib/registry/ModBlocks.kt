package btpos.mcmods.dungeondesignerlib.registry

import btpos.mcmods.devutil.forge.registry.IBlockRegistry
import btpos.mcmods.devutil.forge.registry.IObjectRegistry
import btpos.mcmods.dungeondesignerlib.MODID
import btpos.mcmods.dungeondesignerlib.blocks.BlockDungeonNexus
import btpos.mcmods.dungeondesignerlib.blocks.TileDungeonNexus
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.ObjectHolderDelegate
import thedarkcolour.kotlinforforge.forge.registerObject



object ModBlocks : IBlockRegistry {
	override val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID)
	override val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID)
	override val ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID)
	
	val DUNGEON_NEXUS by block("dungeon_nexus") { BlockDungeonNexus(BlockBehaviour.Properties.of()) }
	val DUNGEON_NEXUS_ITEM by item(::DUNGEON_NEXUS)
	val DUNGEON_NEXUS_ENTITY by ent(::DUNGEON_NEXUS, ::TileDungeonNexus)
}