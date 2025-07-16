package btpos.mcmods.devutil.common.structure

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Just automates the process of marking dirty when a value is set
 */
interface ITileState {
	val onChange: () -> Unit
	
	fun <T> notify(initialValue: T): ReadWriteProperty<Any?, T> {
		return object : ReadWriteProperty<Any?, T> {
			var value: T = initialValue
			
			override fun getValue(thisRef: Any?, property: KProperty<*>) = value
			
			override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
				this.value = value
				onChange()
			}
		}
	}
}