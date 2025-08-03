package btpos.mcmods.dungeondesignerlib

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.Level

object ClientEntry {
	fun init(event: FMLClientSetupEvent) {
		MOD_LOGGER.log(Level.INFO, "Initializing client... with ExampleMod!")
	}
}