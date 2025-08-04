package btpos.mcmods.dungeondesigner.debugging


import btpos.mcmods.devutil.common.macros.brigadier.literal
import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.ext.vanilla.plus
import btpos.mcmods.devutil.common.macros.ChatUtils
import btpos.mcmods.dungeondesigner.builder.items.ItemEntityPipette
import btpos.mcmods.dungeondesigner.builder.nbt.toComponent
import btpos.mcmods.dungeondesigner.builder.nbt.trySpawnEntity
import btpos.mcmods.dungeondesigner.builder.world.dungeonBuilderData
import btpos.mcmods.dungeondesigner.debugging.subcommands.BlockCommands
import btpos.mcmods.dungeondesigner.debugging.subcommands.ItemCommands
import btpos.mcmods.dungeondesigner.registry.ModItems
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.ChatFormatting
import com.mojang.brigadier.context.CommandContext as MojCtx
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component

typealias CommandBuilder = LiteralArgumentBuilder<CommandSourceStack>
typealias CommandContext = MojCtx<CommandSourceStack>

object DebugCommands : CommandHandler {
    fun make(): CommandBuilder {
        return literal("ddl") {
            then(makeFlagCommand())
            then(makePipetteCommand())
            then(BlockCommands.make())
            +ItemCommands.make()
        }
    }
    
    
    private fun makeFlagCommand(): CommandBuilder {
        return literal("flag") {
            "get" {
                StringArgumentType.string()("name") {
                    executes { ctx ->
                        val level = ctx.source.level
                        val data = level.dataStorage.dungeonBuilderData
                        val flagName = StringArgumentType.getString(ctx, "name")
                        
                        if (flagName in data.state.flagStates) {
                            ctx.source.sendSuccess(
                                { "Flag ${flagName}: ${data.getFlag(flagName)}".asComponent() },
                                true
                            )
                        } else {
                            ctx.source.sendSuccess({ "Flag $flagName does not exist.".asComponent() }, true)
                        }
                        
                        return@executes 1
                    }
                }
            }
            "set" {
                StringArgumentType.string()("name") {
                    BoolArgumentType.bool()("value") {
                        executes { ctx ->
                            val level = ctx.source.level
                            val data = level.dataStorage.dungeonBuilderData
                            val flagName = StringArgumentType.getString(ctx, "name")
                            val value = BoolArgumentType.getBool(ctx, "value")
                            
                            data.setFlag(flagName, value)
                            
                            ctx.source.sendSuccess({ "Flag $flagName set to $value".asComponent() }, true)
                            
                            return@executes 1
                        }
                    }
                }
            }
            "list" {
                executes { ctx ->
                    val data = ctx.source.level.dataStorage.dungeonBuilderData
                    val out = buildString {
                        for ((k, v) in data.state.flagStates) {
                            append(k).append(": ").append(v).append("\n")
                        }
                    }.asComponent()
                    
                    ctx.source.sendSuccess({ out }, true)
                    return@executes 1
                }
            }
            "clear" {
                executes { ctx ->
                    val data = ctx.source.level.dataStorage.dungeonBuilderData
                    data.state.flagStates.clear()
                    ctx.source.sendSuccess({ "Cleared flags.".asComponent() }, true)
                    return@executes 1
                }
            }
        }
    }
    
    private fun makePipetteCommand(): CommandBuilder {
        return literal("pipette") {
            requires {
                it.isPlayer && it.playerOrException.mainHandItem.item == ModItems.PIPETTE_ITEM
            }
            "spawn" {
                requires {
                    it.playerOrException.mainHandItem.let { ItemEntityPipette.isReadyToSpawn(it) }
                }
                executes(::spawnFromPipette)
            }
            "get" {
                executes { ctx ->
                    val itemNbt = ctx.source
                            .playerOrException
                            .mainHandItem
                            .let(ItemEntityPipette::getDataOrNull)
                        ?: return@executes 0
                    
                    ctx.source.sendSuccess({ itemNbt.toComponent() }, true)
                    return@executes 1
                }
            }
        }
    }
    
    private fun spawnFromPipette(ctx: CommandContext): Int {
        val stack = ctx.source.playerOrException.mainHandItem
        require(stack.item == ModItems.PIPETTE_ITEM)
        
        val data = ItemEntityPipette.getDataOrNull(stack) ?: return 0
        val res = data.trySpawnEntity(ctx.source.level)
        when (res) {
            null -> {
                ctx.source.sendFailure("Failed to spawn entity.".asComponent())
                return 0
            }
            else -> {
                val msg: () -> Component = {
                    with (data) {
                        with (ChatUtils) {
                            -"Spawned entity with UUID '" + (-res.toString())[ChatFormatting.BLUE] +
                                    " at position " + pos.toComponent()[ChatFormatting.YELLOW] +
                                    " with rotation " + (-rotation.toString())[ChatFormatting.YELLOW] + " degrees."
                        }
                    }
                }
                ctx.source.sendSuccess(msg, true)
                return 1
            }
        }
    }
}

interface CommandHandler {
    /**
     * Macro for returning the failure message method from within the command instead of having two lines each time
     */
    fun CommandContext.sendFailure(msg: String): Int {
        return sendFailure(msg.asComponent())
    }
    fun CommandContext.sendFailure(msg: Component): Int {
        this.source.sendFailure(msg)
        return 0
    }
    
    /**
     * Macro for returning the success message method from within the command instead of having two lines each time
     */
    fun CommandContext.sendSuccess(msg: () -> Component, allowLogging: Boolean = true): Int {
        this.source.sendSuccess(msg, allowLogging)
        return 1
    }
}