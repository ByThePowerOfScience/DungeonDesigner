package btpos.mcmods.dungeondesignerlib.registry

import btpos.mcmods.devutil.forge.registry.IObjectRegistry
import btpos.mcmods.dungeondesignerlib.MODID
import btpos.mcmods.dungeondesignerlib.builder.items.ItemEntityPipette
import btpos.mcmods.dungeondesignerlib.builder.items.ItemFlagVariable
import btpos.mcmods.dungeondesignerlib.builder.items.ItemTriggerVariable
import net.minecraft.world.item.Item
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject

object ModItems : IObjectRegistry<Item> {
	override val REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MODID)
	
	private val variable_props = Item.Properties().fireResistant()
	
	val TRIGGER_ITEM by REGISTRY.registerObject(ItemTriggerVariable.id) { ItemTriggerVariable(variable_props) }
	val FLAG_ITEM by REGISTRY.registerObject(ItemFlagVariable.id) { ItemFlagVariable(variable_props) }
	val PIPETTE_ITEM by REGISTRY.registerObject(ItemEntityPipette.id) { ItemEntityPipette(variable_props) }
}