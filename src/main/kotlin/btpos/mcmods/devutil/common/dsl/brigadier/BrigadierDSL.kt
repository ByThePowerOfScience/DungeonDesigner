@file:Suppress("NOTHING_TO_INLINE", "unused")

package btpos.mcmods.devutil.common.dsl.brigadier

import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.SingleRedirectModifier
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode

import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate


object Command {
	/**
	 * Start a command tree.
	 * Usage:
	 * ```kotlin
	 * literal("foo") {
	 *    literal("bar") {
	 *       argument("pos", BlockPosArgument.blockPos()) {
	 *          executes { ctx ->
	 *              ctx.sendSuccess("yay!")
	 *              return@executes 1
	 *          }
	 *       }
	 *    }
	 * }
	 * ```
	 */
	inline fun <T> literal(name: String, crossinline then: LiteralBuilder<T>.() -> Unit): LiteralArgumentBuilder<T> {
		return LiteralBuilder(LiteralArgumentBuilder.literal<T>(name)).apply(then).internal
	}
	
	
	/**
	 * Start an argument node.  You shouldn't be giving this to a dispatcher, instead using this to create reusable argument trees.
	 */
	inline fun <SOURCE, T> argument(
		name: String,
		type: ArgumentType<T>,
		crossinline then: ArgBuilder<SOURCE, T>.() -> Unit
	): RequiredArgumentBuilder<SOURCE, T> {
		return ArgBuilder(RequiredArgumentBuilder.argument<SOURCE, T>(name, type)).apply(then).internal
	}
}

/**
 * Helper function for optional argument retrieval.
 */
inline fun <T : Any> CommandContext<*>.optionalArgument(name: String, type: Class<T>): Optional<T> {
	return try {
		Optional.of(this.getArgument(name, type))
	} catch (e: Exception) {
		Optional.empty()
	}
}

inline fun <reified T : Any> CommandContext<*>.optionalArgument(name: String): T? {
	return try {
		this.getArgument(name, T::class.java)
	} catch (_: Exception) {
		null
	}
}




@DslMarker
annotation class BrigadierDSL

@JvmInline @BrigadierDSL
value class ArgBuilder<S, T>(val internal: RequiredArgumentBuilder<S, T>) {
	/**
	 * Add an argument after this one.  See the subclasses of ArgumentType for more information.
	 */
	inline fun <U> argument(arg: String, type: ArgumentType<U>, 
	                        crossinline then: ArgBuilder<S, U>.() -> Unit) {
		val it = ArgBuilder<S, U>(RequiredArgumentBuilder.argument(arg, type)).apply(then)
		internal.then(it.internal)
	}
	
	inline fun <T> arg(name: String, type: ArgumentType<T>, crossinline then: ArgBuilder<S, T>.() -> Unit) = argument(name, type, then)
	
	inline operator fun <T> ArgumentType<T>.invoke(name: String, crossinline then: ArgBuilder<S, T>.() -> Unit) = argument(name, this, then)
	
	
	
	inline operator fun String.invoke(crossinline then: LiteralBuilder<S>.() -> Unit) = literal(this, then)
	
	
	/**
	 * Start a new branch (or continue the only branch) with a literal word as the designator, e.g. a command "foo" with a subcommand "bar" would be "literal("foo") { literal("bar") { ... } }"
	 */
	inline fun literal(name: String, 
	                   crossinline then: LiteralBuilder<S>.() -> Unit) {
		val it = LiteralBuilder<S>(LiteralArgumentBuilder.literal(name)).apply(then)
		internal.then(it.internal)
	}
	
	/**
	 * Condition the user of this command or any subcommands must satisfy to use it.
	 */
	inline fun requires(noinline condition: (S) -> Boolean) {
		internal.requires(condition)
	}
	
	/**
	 * Condition the user of this command or any subcommands must satisfy to use it.
	 */
	inline fun requires(condition: Predicate<S>) {
		internal.requires(condition)
	}
	
	/**
	 * The action this command path performs. Should return 1 on success and 0 on failure. (See Command.SINGLE_SUCCESS)
	 */
	inline fun executes(command: Command<S>) {
		internal.executes(command)
	}
	
	/**
	 * The action this command path performs. Should return 1 on success and 0 on failure. (See Command.SINGLE_SUCCESS)
	 */
	inline fun executes(noinline command: (CommandContext<S>) -> Int) {
		internal.executes(command)
	}
	
	/**
	 * Add suggestions to the builder and return builder.buildFuture()
	 */
	inline fun suggests(provider: SuggestionProvider<S>) {
		internal.suggests(provider)
	}
	
	/**
	 * Add suggestions to the builder and return builder.buildFuture()
	 */
	inline fun suggests(noinline provider: (CommandContext<S>, SuggestionsBuilder) -> CompletableFuture<Suggestions>) {
		internal.suggests(provider)
	}
	
	/**
	 * Finalize this builder into a command node for reuse in other "then", "fork", "redirect", etc. calls.
	 */
	inline fun build(): ArgumentCommandNode<S, T> {
		return internal.build()
	}
	
	/**
	 * Append a branching execution path to this tree from a precompiled node.
	 */
	inline fun then(argument: CommandNode<S>) {
		internal.then(argument)
	}
	
	/**
	 * Append a branching execution path to this tree.
	 * These are called implicitly with "argument(...) {  }" or "literal(...) {  }" for neater writing, but these still exist in case you need them for any reason.
	 */
	inline fun then(argument: ArgumentBuilder<S, *>) {
		internal.then(argument)
	}
	
	/**
	 * Directs execution to the given target node (acquired with ArgumentBuilder#build) when parsed, applying the provided modifier to the command context before proceeding to the redirect target.
	 * I'd imagine it's used for things like adding optional arguments to the front of a command, like "dothing thisway x y z" instead of "dothing x y z".
	 * E.g. you'd turn "x y z" into a commandnode, and have just "dothing x y z" be a redirect from "dothing" to "x y z" with the modifier being a default value for "thisway".
	 */
	inline fun redirect(target: CommandNode<S>, modifier: SingleRedirectModifier<S>? = null) {
		internal.redirect(target, modifier)
	}
	
	inline fun redirect(target: CommandNode<S>, noinline modifier: (CommandContext<S>) -> S) {
		internal.redirect(target, modifier)
	}
	
	/**
	 * "If the command passes through a node that is {@link CommandNode#isFork()} then it will be 'forked'.
	 * A forked command will not bubble up any {@link CommandSyntaxException}s, and the 'result' returned will turn into
	 * 'amount of successful commands executes'."
	 *
	 * Essentially, forked nodes just don't propagate exceptions, and act like batch jobs in powershell instead.
	 */
	inline fun fork(target: CommandNode<S>, modifier: RedirectModifier<S>) {
		internal.fork(target, modifier)
	}
	
	/**
	 * Direct access to the internals of redirect and fork.
	 */
	inline fun forward(target: CommandNode<S>, modifier: RedirectModifier<S>, fork: Boolean) {
		internal.forward(target, modifier, fork)
	}
}


@JvmInline @BrigadierDSL
value class LiteralBuilder<S>(val internal: LiteralArgumentBuilder<S>) {
	/**
	 * Add an argument after this one.  See the subclasses of [ArgumentType] for more information.
	 */
	inline fun <T> argument(name: String, type: ArgumentType<T>, crossinline then: ArgBuilder<S, T>.() -> Unit) {
		val it = ArgBuilder<S, T>(RequiredArgumentBuilder.argument(name, type)).apply(then)
		internal.then(it.internal)
	}
	
	inline fun <T> arg(name: String, type: ArgumentType<T>, crossinline then: ArgBuilder<S, T>.() -> Unit) = argument(name, type, then)
	
	inline operator fun <T> ArgumentType<T>.invoke(name: String, crossinline then: ArgBuilder<S, T>.() -> Unit) = argument(name, this, then)
	
	
	
	inline operator fun String.invoke(crossinline then: LiteralBuilder<S>.() -> Unit) = literal(this, then)
	
	/**
	 * Start a new branch (or continue the only branch) with a literal word as the designator, e.g. "/foo bar" - a command "foo" with a subcommand "bar" - would be "literal("foo") { literal("bar") { ... } }"
	 */
	inline fun literal(name: String, crossinline then: LiteralBuilder<S>.() -> Unit) {
		val it = LiteralBuilder<S>(LiteralArgumentBuilder.literal(name)).apply(then)
		internal.then(it.internal)
	}
	
	/**
	 * Append a branching execution path to this tree from a precompiled node.
	 */
	inline fun then(argument: CommandNode<S>) {
		internal.then(argument)
	}
	
	/**
	 * Append a branching execution path to this tree.
	 * Called implicitly with "argument(...) {  }" or "literal(...) {  }" for neater writing, but these still exist in case you need them for any reason.
	 */
	inline fun then(argument: ArgumentBuilder<S, *>) {
		internal.then(argument)
	}
	
	/**
	 * Directs execution to the given target node (acquired with [ArgumentBuilder.build]) when parsed, applying the provided modifier to the command context before proceeding to the redirect target.
	 * I'd imagine it's used for things like adding optional arguments to the front of a command, like "dothing thisway x y z" instead of "dothing x y z".
	 * E.g. you'd turn "x y z" into a commandnode, and have just "dothing x y z" be a redirect from "dothing" to "x y z" with the modifier being a default value for "thisway".
	 */
	inline fun redirect(target: CommandNode<S>, modifier: SingleRedirectModifier<S>? = null) {
		internal.redirect(target, modifier)
	}
	
	/**
	 * "If the command passes through a node that is [CommandNode.isFork] then it will be 'forked'.
	 * A forked command will not bubble up any [com.mojang.brigadier.exceptions.CommandSyntaxException]s, and the 'result' returned will turn into
	 * 'amount of successful commands executes'."
	 *
	 * Essentially, forked nodes just don't propagate exceptions, and act like batch jobs in powershell instead.
	 * Or I guess "fork" in Linux would be more appropriate.
	 */
	inline fun fork(target: CommandNode<S>, modifier: RedirectModifier<S>) {
		internal.fork(target, modifier)
	}
	
	/**
	 * Direct access to the internals of redirect and fork.
	 */
	inline fun forward(target: CommandNode<S>, modifier: RedirectModifier<S>, fork: Boolean) {
		internal.forward(target, modifier, fork)
	}
	
	/**
	 * The action this command path performs. Should return 1 on success and 0 on failure. (See Command.SINGLE_SUCCESS)
	 */
	inline fun executes(command: Command<S>) {
		internal.executes(command)
	}
	
	/**
	 * The action this command path performs. Should return 1 on success and 0 on failure. (See Command.SINGLE_SUCCESS)
	 */
	inline fun executes(noinline command: (CommandContext<S>) -> Int) {
		internal.executes(command)
	}
	
	/**
	 * Finalize this builder into a command node for reuse in other "then", "fork", "redirect", etc. calls.
	 */
	inline fun build(): LiteralCommandNode<S> {
		return internal.build()
	}
	
	/**
	 * Condition the user of this command or any subcommands must satisfy to use it.
	 */
	inline fun requires(noinline condition: (S) -> Boolean) {
		internal.requires(condition)
	}
	
	/**
	 * Condition the user of this command or any subcommands must satisfy to use it.
	 */
	inline fun requires(condition: Predicate<S>) {
		internal.requires(condition)
	}
}

