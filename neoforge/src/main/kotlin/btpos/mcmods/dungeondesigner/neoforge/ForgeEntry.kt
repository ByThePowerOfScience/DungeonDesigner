package btpos.mcmods.dungeondesigner.neoforge

import btpos.mcmods.dungeondesigner.ClientEntry
import btpos.mcmods.dungeondesigner.CommonEntry
import btpos.mcmods.dungeondesigner.MODID
import btpos.mcmods.dungeondesigner.datagen.DataGenConstants
import btpos.mcmods.dungeondesigner.registry.ModBlocks
import dev.architectury.platform.hooks.EventBusesHooks
import net.neoforged.bus.EventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(MODID)
object ForgeEntry {
	init {
		CommonEntry.init()
	}
}