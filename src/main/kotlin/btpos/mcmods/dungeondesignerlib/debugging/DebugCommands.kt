package btpos.mcmods.dungeondesignerlib.debugging

import btpos.mcmods.devutil.common.dsl.brigadier.literal
import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.dungeondesignerlib.builder.world.dungeonBuilderData
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack

typealias CommandBuilder = LiteralArgumentBuilder<CommandSourceStack>

object DebugCommands {
	fun make(): CommandBuilder {
		return literal("ddl") {
			"debug" {
				then(makeFlagCommand())
			}
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
							ctx.source.sendSuccess({ "Flag ${flagName}: ${data.getFlag(flagName)}".asComponent() }, true)
						} else {
							ctx.source.sendSuccess({"Flag ${flagName} does not exist.".asComponent()}, true)
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
}