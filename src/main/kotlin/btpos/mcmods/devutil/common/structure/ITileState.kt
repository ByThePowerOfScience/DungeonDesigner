package btpos.mcmods.devutil.common.structure

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Utilities for compositional tile state.
 */
interface ITileState {
	/**
	 * Callback to be invoked whenever this state is changed.
	 */
	val onChange: () -> Unit
	
	/**
	 * Property delegate that calls [::onChange] when the value has been set.
	 */
	fun <T> notify(initialValue: T): ReadWriteProperty<Any?, T> {
		return object : ReadWriteProperty<Any?, T> {
			var value: T = initialValue
			
			override fun getValue(thisRef: Any?, property: KProperty<*>) = value
			
			override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
				if (this.value == value)
					return;
				
				this.value = value
				onChange()
			}
		}
	}
}