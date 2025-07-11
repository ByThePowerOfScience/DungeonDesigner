package btpos.mcmods.devutil.forge.datagen

import btpos.mcmods.dungeondesignerlib.MODID
import net.minecraft.resources.ResourceLocation

interface IObjectData {
	val id: String
	
	val resourceLocation get() = ResourceLocation(MODID, this.id)
}

