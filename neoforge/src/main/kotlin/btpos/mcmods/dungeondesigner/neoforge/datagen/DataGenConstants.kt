@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package btpos.mcmods.dungeondesigner.neoforge.datagen

import btpos.mcmods.dungeondesigner.MODID
import btpos.mcmods.dungeondesigner.neoforge.datagen.lang.LangGen_English
import btpos.mcmods.dungeondesigner.neoforge.datagen.models.ModelDataGen
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent


@EventBusSubscriber(modid=MODID, value=[Dist.CLIENT])
object DataGenConstants {
	@SubscribeEvent
	fun gatherDataEvent(evt: GatherDataEvent.Client) {
		evt.createProvider(::ModelDataGen)
		evt.createProvider(::LangGen_English)
	}
}