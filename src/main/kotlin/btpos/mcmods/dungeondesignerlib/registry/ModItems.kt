package btpos.mcmods.dungeondesignerlib.registry

import btpos.mcmods.devutil.forge.registry.IObjectRegistry
import btpos.mcmods.dungeondesignerlib.MODID
import net.minecraft.world.item.Item
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject

object ModItems : IObjectRegistry<Item> {
	override val REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MODID)
	
	val TRIGGER_BRUSH:Nothing by REGISTRY.registerObject("") { TODO() }
}