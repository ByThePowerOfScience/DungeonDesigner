package btpos.mcmods.dungeondesignerlib

import btpos.devutil.common.macros.brigadier.literal
import btpos.mcmods.dungeondesignerlib.datagen.DataGenConstants
import btpos.mcmods.dungeondesignerlib.debugging.DebugCommands
import btpos.mcmods.dungeondesignerlib.registry.ModBlocks
import btpos.mcmods.dungeondesignerlib.registry.ModItems
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

const val MODID = "dungeondesigner"
val LOGGER: Logger = LogManager.getLogger(MODID)

@Mod(MODID)
object CommonEntry {
    init {
        LOGGER.log(Level.INFO, "$MODID has started!")

        MOD_BUS.addListener(ClientEntry::init)
        MOD_BUS.addListener(DataGenConstants::gatherDataEvent)
        
        FORGE_BUS.addListener(::registerCommands)
        
        ModBlocks.register()
        ModItems.register()
    }
    
    fun registerCommands(evt: RegisterCommandsEvent) {
        evt.dispatcher.register(DebugCommands.make())
    }
}