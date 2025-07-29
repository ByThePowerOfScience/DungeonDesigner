package btpos.mcmods.devutil.parts

import btpos.mcmods.devutil.misc.KotlinAssignmentOverloadTarget
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.jetbrains.annotations.Contract

/**
 * Wraps some value, and provides functions converting it to and from an [net.minecraft.world.item.ItemStack].
 */
interface IItemRepresentable<T> {
	/**
	 * The ItemStack to be returned if [writeToItem] returns `null`.
	 *
	 * Defaults to [net.minecraft.world.item.ItemStack.EMPTY]
	 */
	val defaultItemStack: ItemStack
		get() = ItemStack.EMPTY
	
	
	/**
	 * The actual value being wrapped.
	 *
	 * This is used as the default assignment operator target, but there can be multiple value fields in this class.
	 */
	var value: T?
	
	/**
	 * The ItemStack representation of [value].
	 *
	 * If the value of [value] is null, returns [defaultItemStack]. (defaults to [ItemStack.EMPTY])
	 */
	var asItem: ItemStack
		get() {
			return writeToItem() ?: defaultItemStack
		}
		set(stack) {
			if (!acceptsItem(stack))
				return;
			
			setFromItem(stack)
		}
	
	/**
	 * Store this object's state in item form.
	 *
	 * Return null to use [defaultItemStack] instead.
	 */
	@Contract(pure=true)
	fun writeToItem(): ItemStack?
	
	/**
	 * Should set the value of [value] based on the ItemStack provided.
	 */
	@Contract(pure=false)
	fun setFromItem(stack: ItemStack)
	
	/**
	 * Check whether this should accept the provided itemstack.
	 *
	 * @param stack The itemstack the caller is attempting to insert into this object.
	 */
	@Contract(pure=true)
	fun acceptsItem(stack: ItemStack): Boolean
}

/**
 * Wraps some value, and provides functions converting it to and from an [net.minecraft.world.item.ItemStack]'s CompoundTag.
 */
@KotlinAssignmentOverloadTarget
interface IItemRepresentable_Tag<T> : IItemRepresentable<T> {
	/**
	 * This will only perform ItemStack deserialization if the stack wraps this item.
	 */
	val defaultItem: Item
	
	override fun acceptsItem(stack: ItemStack): Boolean {
		return stack.`is`(defaultItem)
	}
	
	override fun writeToItem(): ItemStack? {
		if (value == null)
			return null
		return ItemStack(defaultItem).apply {
			getOrCreateTag().writeToTag()
		}
	}
	
	override fun setFromItem(stack: ItemStack) {
		stack.tag?.readFromTag()
	}
	
	/**
	 * Deserialization function. Returns the value read from the ItemStack's root tag, or null if it's not present.
	 *
	 * Called in [asItem]'s setter.
	 */
	fun CompoundTag.readFromTag()
	
	/**
	 * Serialization function. Puts the value into the ItemStack's root tag.
	 *
	 * Called in [asItem]'s getter.
	 */
	fun CompoundTag.writeToTag()
	
	// Assignment Overloading
	@Suppress("UNUSED")
	fun assign(stack: ItemStack) {
		this.asItem = stack
	}
	@Suppress("UNUSED")
	fun assign(t: T?) {
		this.value = t
	}
	
	companion object {
		
		/**
		 * Returns a default dynamic implementation of [IItemRepresentable_Tag].
		 */
		operator fun <T> invoke(
			initialValue: T?,
			defaultItem: Item,
			tagReader: CompoundTag.() -> T?,
			tagWriter: CompoundTag.(T?) -> Unit,
			defaultItemStack: ItemStack = ItemStack.EMPTY
		) : IItemRepresentable_Tag<T> {
			return object : IItemRepresentable_Tag<T> {
				override var value: T? = initialValue
				override val defaultItem = defaultItem
				override val defaultItemStack: ItemStack = defaultItemStack
				
				override fun CompoundTag.readFromTag() {
					value = tagReader()
				}
				
				override fun CompoundTag.writeToTag() {
                    tagWriter(initialValue)
                }
			}
		}
	}
}


