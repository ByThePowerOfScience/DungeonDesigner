package btpos.mcmods.devutil.common.ext.kotlin

inline fun <ITEM, YIELD> Iterable<ITEM>.mapReduce(startingValue: YIELD, reductionFunction: (ITEM, YIELD) -> YIELD): YIELD {
	var yield: YIELD = startingValue
	for (el in this) {
		yield = reductionFunction(el, yield)
	}
	return yield
}

/**
 * Split a list into two lists: ones that match the predicate and ones that don't.
 */
inline fun <ITEM> Iterable<ITEM>.filterSplit(predicate: (ITEM) -> Boolean): FilterResult<ITEM> {
	val matching = mutableListOf<ITEM>()
	val notMatching = mutableListOf<ITEM>()
	for (it in this) {
		if (predicate(it))
			matching += it
		else
			notMatching += it
	}
	
	return FilterResult(matching, notMatching)
}

data class FilterResult<T>(val matching: List<T>, val notMatching: List<T>) {
	inline fun onTrue(action: (List<T>) -> Unit): FilterResult<T> {
		matching.apply(action)
		return this
	}
	
	inline fun onFalse(action: (List<T>) -> Unit): FilterResult<T> {
		notMatching.apply(action)
		return this
	}
}