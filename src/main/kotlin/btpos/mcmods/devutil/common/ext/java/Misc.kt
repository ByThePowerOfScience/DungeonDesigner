@file:Suppress("NOTHING_TO_INLINE")

package btpos.mcmods.devutil.common.ext.java

import java.util.Optional

inline val <T : Any> Optional<T>.value: T?
	inline get() = this.orElse(null)

inline fun <T : Any> T?.asOptional(): Optional<T> = Optional.ofNullable(this)

/**
 * This is clearly a bug in the Kotlin compiler.
 * There's literally no difference between Optional<T & Any> and Optional<T>, because the output type is always T.
 */
@Suppress("UNCHECKED_CAST")
inline fun <U, T : U & Any> Optional<T>.cast() = this as Optional<U>

