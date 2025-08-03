@file:Suppress("NOTHING_TO_INLINE")

package btpos.mcmods.devutil.common.macros

inline fun panic(message: String = "") : Nothing {
	throw IllegalStateException(message)
}