package btpos.mcmods.devutil.common.ext.kotlin

inline fun <ITEM, YIELD> Iterable<ITEM>.mapReduce(startingValue: YIELD, reductionFunction: (ITEM, YIELD) -> YIELD): YIELD {
	var yield: YIELD = startingValue
	for (el in this) {
		yield = reductionFunction(el, yield)
	}
	return yield
}