package btpos.mcmods.dungeondesigner.registry

import btpos.mcmods.devutil.common.registry.IItemRegistry
import btpos.mcmods.devutil.common.registry.registerObject
import btpos.mcmods.dungeondesigner.builder.items.ItemEntityPipette
import btpos.mcmods.dungeondesigner.builder.items.ItemFlagVariable
import btpos.mcmods.dungeondesigner.builder.items.ItemTriggerVariable
import btpos.mcmods.dungeondesigner.builder.redstone.items.ItemRemoteLinker
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item

object ModItems : IItemRegistry {
	override val ITEMS = createRegistry(Registries.ITEM)
	
	private val variable_props = { Item.Properties().fireResistant() }
	
	val TRIGGER_ITEM by item(ItemTriggerVariable.id, variable_props) { ItemTriggerVariable(it) }
	val FLAG_ITEM by item(ItemFlagVariable.id, variable_props) { ItemFlagVariable(it) }
	val PIPETTE_ITEM by item(ItemEntityPipette.id, variable_props) { ItemEntityPipette(it) }
	
	val REMOTE_LINKER by item(ItemRemoteLinker.id, variable_props) { ItemRemoteLinker(it) }
}