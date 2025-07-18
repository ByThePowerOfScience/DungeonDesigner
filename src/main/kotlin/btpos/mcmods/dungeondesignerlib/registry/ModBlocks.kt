package btpos.mcmods.dungeondesignerlib.registry

import btpos.mcmods.devutil.forge.registry.IBlockRegistry
import btpos.mcmods.dungeondesignerlib.MODID
import btpos.mcmods.dungeondesignerlib.builder.blocks.BlockDungeonNexus
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.BlockTriggerHolder
import btpos.mcmods.dungeondesignerlib.builder.blocks.TileDungeonNexus
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.BlockFlagReader
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.BlockFlagWriter
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.TileFlagHolder
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.TileTriggerHolder
import com.mojang.datafixers.types.Type
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries


object ModBlocks : IBlockRegistry {
	override val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID)
	override val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID)
	override val ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID)
	
	val dungeonLogicProperties = BlockBehaviour.Properties.of()
		.pushReaction(PushReaction.IGNORE)
		.mapColor(MapColor.COLOR_MAGENTA)
		.strength(-1.0F, 3600000.0F)
		.noLootTable()
		.isValidSpawn { _, _, _, _ -> false }
	
	val DUNGEON_NEXUS by block(BlockDungeonNexus.id, withItem=true) { BlockDungeonNexus(dungeonLogicProperties) }
	val DUNGEON_NEXUS_ENTITY by ent(::DUNGEON_NEXUS, ::TileDungeonNexus)
	
	val TRIGGER_BLOCK by block(BlockTriggerHolder.id, withItem=true) { BlockTriggerHolder(dungeonLogicProperties) }
	val TRIGGER_BLOCK_ENTITY by ent(::TRIGGER_BLOCK, ::TileTriggerHolder)
	
	val FLAG_READER by block("flag_reader", withItem=true) { BlockFlagReader(dungeonLogicProperties) }
	
	/**
	 * Turns the flag ON
	 */
	val FLAG_SETTER by block("flag_setter", withItem=true) { BlockFlagWriter(dungeonLogicProperties, true) }
	
	/**
	 * Turns the flag OFF
	 */
	val FLAG_RESETTER by block("flag_resetter", withItem=true) { BlockFlagWriter(dungeonLogicProperties, false) }
	val FLAG_BLOCK_ENTITY by ent("flag_block") { BlockEntityType.Builder.of(::TileFlagHolder, FLAG_READER, FLAG_SETTER, FLAG_RESETTER).build(null) }
}