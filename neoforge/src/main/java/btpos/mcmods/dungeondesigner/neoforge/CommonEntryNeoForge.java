package btpos.mcmods.dungeondesigner.neoforge;

import btpos.mcmods.dungeondesigner.CommonEntry;
import net.neoforged.fml.common.Mod;

@Mod(CommonEntry.MOD_ID)
public final class CommonEntryNeoForge {
    public CommonEntryNeoForge() {
        // Run our common setup.
        CommonEntry.init();
    }
}
