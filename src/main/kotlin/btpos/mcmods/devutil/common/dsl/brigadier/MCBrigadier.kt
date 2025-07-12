package btpos.mcmods.devutil.common.dsl.brigadier

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import btpos.mcmods.devutil.common.dsl.brigadier.Command.argument as genericArg
import btpos.mcmods.devutil.common.dsl.brigadier.Command.literal as genericLit

typealias CommandLiteral = LiteralArgumentBuilder<CommandSourceStack>
typealias CommandArgument<T> = RequiredArgumentBuilder<CommandSourceStack, T>

inline fun literal(name: String, crossinline then: LiteralBuilder<CommandSourceStack>.() -> Unit): CommandLiteral = genericLit(name, then)
inline fun <T> argument(name: String, type: ArgumentType<T>, crossinline then: ArgBuilder<CommandSourceStack, T>.() -> Unit) = genericArg(name, type, then)
