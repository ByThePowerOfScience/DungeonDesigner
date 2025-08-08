package btpos.mcmods.dungeondesigner.neoforge

import btpos.mcmods.dungeondesigner.CommonEntry
import btpos.mcmods.dungeondesigner.MODID
import net.neoforged.fml.common.Mod

@Mod(MODID)
object ForgeEntry {
	init {
		CommonEntry.init()
	}
}