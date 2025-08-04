package btpos.mcmods.dungeondesigner.neoforge

import btpos.mcmods.dungeondesigner.ClientEntry
import btpos.mcmods.dungeondesigner.MODID
import btpos.mcmods.dungeondesigner.datagen.DataGenConstants
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(MODID)
object ForgeEntry {
	init {
		MOD_BUS.addListener { evt: FMLClientSetupEvent -> ClientEntry.init() }
        MOD_BUS.addListener(DataGenConstants::gatherDataEvent)
	}
}