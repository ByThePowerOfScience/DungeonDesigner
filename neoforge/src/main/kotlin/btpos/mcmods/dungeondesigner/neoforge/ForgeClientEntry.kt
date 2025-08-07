package btpos.mcmods.dungeondesigner.neoforge

import btpos.mcmods.dungeondesigner.ClientEntry
import btpos.mcmods.dungeondesigner.MODID
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent

@EventBusSubscriber(modid=MODID, value=[Dist.CLIENT])
object ForgeClientEntry {
	@SubscribeEvent
	fun clientEntry(evt: FMLClientSetupEvent) {
		ClientEntry.init()
	}
}