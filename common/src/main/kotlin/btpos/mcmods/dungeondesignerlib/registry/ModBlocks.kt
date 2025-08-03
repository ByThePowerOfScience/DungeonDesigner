package btpos.mcmods.dungeondesignerlib.registry

import btpos.mcmods.devutil.forge.registry.IBlockRegistry
import btpos.mcmods.dungeondesignerlib.MODID
import btpos.mcmods.dungeondesignerlib.builder.blocks.BlockDungeonNexus
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.BlockTriggerHolder
import btpos.mcmods.dungeondesignerlib.builder.blocks.TileDungeonNexus
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.BlockFightController
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.BlockFlagReader
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.BlockFlagWriter
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.TileFightController
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.TileFlagHolder
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.TileTriggerHolder
import btpos.mcmods.dungeondesignerlib.builder.redstone.blocks.BlockRedstoneReceiver
import btpos.mcmods.dungeondesignerlib.builder.redstone.blocks.BlockRedstoneTransmitter
import btpos.mcmods.dungeondesignerlib.builder.redstone.blocks.ItemBlockRedstoneReceiver
import btpos.mcmods.dungeondesignerlib.builder.redstone.blocks.ItemBlockRedstoneTransmitter
import btpos.mcmods.dungeondesignerlib.builder.redstone.blocks.TileRedstoneReceiver
import btpos.mcmods.dungeondesignerlib.builder.redstone.blocks.TileRedstoneTransmitter
import net.minecraft.world.item.Item
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
	
	val logicBlockProperties = BlockBehaviour.Properties.of()
		.pushReaction(PushReaction.IGNORE)
		.mapColor(MapColor.COLOR_MAGENTA)
		.strength(-1.0F, 3600000.0F)
		.noLootTable()
		.isValidSpawn { _, _, _, _ -> false }
	
	val logicItemProperties = Item.Properties().fireResistant()
	
	val DUNGEON_NEXUS by block(BlockDungeonNexus.id, withItem=true) { BlockDungeonNexus(logicBlockProperties) }
	val DUNGEON_NEXUS_ENTITY by ent(::DUNGEON_NEXUS, ::TileDungeonNexus)
	
	val TRIGGER_BLOCK by block(BlockTriggerHolder.id, withItem=true) { BlockTriggerHolder(logicBlockProperties) }
	val TRIGGER_BLOCK_ENTITY by ent(::TRIGGER_BLOCK, ::TileTriggerHolder)
    
    //region Flag Blocks
    /**
	 * Emits signal when the flag is on
	 */
	val FLAG_READER by block("flag_reader", withItem=true) { BlockFlagReader(logicBlockProperties) }
	
	/**
	 * Turns the flag ON
	 */
	val FLAG_SETTER by block("flag_setter", withItem=true) { BlockFlagWriter(logicBlockProperties, true) }
	
	/**
	 * Turns the flag OFF
	 */
	val FLAG_RESETTER by block("flag_resetter", withItem=true) { BlockFlagWriter(logicBlockProperties, false) }
	
	val FLAG_BLOCK_ENTITY by ent("flag_block") { BlockEntityType.Builder.of(::TileFlagHolder, FLAG_READER, FLAG_SETTER, FLAG_RESETTER).build(null) }
    //endregion
	
	
	
	val FIGHT_CONTROLLER by block(BlockFightController.id, withItem=true) { BlockFightController(logicBlockProperties) }
	val FIGHT_CONTROLLER_ENTITY by ent(::FIGHT_CONTROLLER, ::TileFightController)
	
	
	
	val REDSTONE_RECEIVER by block(BlockRedstoneReceiver.id, withItem=false) { BlockRedstoneReceiver(logicBlockProperties) }
	val REDSTONE_RECEIVER_ENTITY by ent(::REDSTONE_RECEIVER, ::TileRedstoneReceiver)
	val REDSTONE_RECEIVER_ITEM by item(::REDSTONE_RECEIVER) { ItemBlockRedstoneReceiver(logicItemProperties) }
	
	val REDSTONE_TRANSMITTER by block(BlockRedstoneTransmitter.id, withItem=false) { BlockRedstoneTransmitter(logicBlockProperties) }
	val REDSTONE_TRANSMITTER_ENTITY by ent(::REDSTONE_TRANSMITTER, ::TileRedstoneTransmitter)
	val REDSTONE_TRANSMITTER_ITEM by item(::REDSTONE_TRANSMITTER) { ItemBlockRedstoneTransmitter(logicItemProperties) }
}