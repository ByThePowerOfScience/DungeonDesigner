package btpos.mcmods.devutil.common.util

interface IReverseCloneable<T> {
	/**
	 * Populates the internal state of `this` to match the internal state of `other`.
	 */
	fun copyFrom(other: T)
}