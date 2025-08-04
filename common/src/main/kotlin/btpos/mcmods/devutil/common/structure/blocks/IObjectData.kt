package btpos.mcmods.devutil.common.structure.blocks

import btpos.mcmods.dungeondesigner.MODID
import net.minecraft.resources.ResourceLocation

interface IObjectData {
	val id: String
	
	val resourceLocation get() = modLoc(this.id)
	
	fun modLoc(path: String) = ResourceLocation.fromNamespaceAndPath(MODID, path)
}