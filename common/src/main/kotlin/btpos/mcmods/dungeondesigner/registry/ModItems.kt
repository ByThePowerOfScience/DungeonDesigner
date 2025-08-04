package btpos.mcmods.dungeondesigner.registry

import btpos.mcmods.devutil.common.registry.IObjectRegistry
import btpos.mcmods.devutil.common.registry.registerObject
import btpos.mcmods.dungeondesigner.builder.items.ItemEntityPipette
import btpos.mcmods.dungeondesigner.builder.items.ItemFlagVariable
import btpos.mcmods.dungeondesigner.builder.items.ItemTriggerVariable
import btpos.mcmods.dungeondesigner.builder.redstone.items.ItemRemoteLinker
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item

object ModItems : IObjectRegistry<Item> {
	override val REGISTRY = createRegistry(Registries.ITEM)
	
	private val variable_props = Item.Properties().fireResistant()
	
	val TRIGGER_ITEM by REGISTRY.registerObject(ItemTriggerVariable.id) { ItemTriggerVariable(variable_props) }
	val FLAG_ITEM by REGISTRY.registerObject(ItemFlagVariable.id) { ItemFlagVariable(variable_props) }
	val PIPETTE_ITEM by REGISTRY.registerObject(ItemEntityPipette.id) { ItemEntityPipette(variable_props) }
	
	val REMOTE_LINKER by registering(ItemRemoteLinker.id) { ItemRemoteLinker(variable_props) }
}