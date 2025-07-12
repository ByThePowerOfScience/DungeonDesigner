package btpos.mcmods.devutil.common.ext.kotlin

inline fun <T> T?.ifNull(action: () -> Unit): T? {
	if (this == null)
		action()
	return this
}

inline fun <T> T.runIf(condition: Boolean, action: T.() -> T): T {
	if (condition)
		return action(this)
	else
		return this
}

inline fun <T> T.alsoRun(action: T.() -> Unit): T {
	this.action()
	return this
}