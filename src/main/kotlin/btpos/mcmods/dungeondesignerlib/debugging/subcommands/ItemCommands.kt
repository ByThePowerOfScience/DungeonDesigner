package btpos.mcmods.dungeondesignerlib.debugging.subcommands

import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.macros.brigadier.literal
import btpos.mcmods.devutil.common.macros.brigadier.sendSuccess
import btpos.mcmods.dungeondesignerlib.builder.items.ItemEntityPipette
import btpos.mcmods.dungeondesignerlib.builder.nbt.toComponent
import btpos.mcmods.dungeondesignerlib.registry.ModItems

object ItemCommands {
    fun make() = literal("hand") {
        executes { ctx ->
            val heldStack = ctx.source.playerOrException.mainHandItem
            when (heldStack.item) {
                ModItems.PIPETTE_ITEM -> {
                    ctx.sendSuccess({ ItemEntityPipette.getDataOrNull(heldStack)?.toComponent() ?: "null".asComponent() })
                }
            }
            
            return@executes 1
        }
    }
}