package btpos.mcmods.devutil.parts

import btpos.mcmods.devutil.common.ext.vanilla.world.dropItem
import btpos.mcmods.devutil.misc.KotlinAssignmentOverloadTarget
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3

/**
 * Wraps some value, and provides functions converting it to and from an [net.minecraft.world.item.ItemStack].
 */
interface IItemRepresentable<T> {
	/**
	 * The ItemStack to be returned if the value is null.
	 * Defaults to [net.minecraft.world.item.ItemStack.EMPTY]
	 */
	val ifNull: ItemStack
		get() = ItemStack.EMPTY
	
	
	/**
	 * The actual value being wrapped.
	 */
	var value: T?
	
	/**
	 * The ItemStack representation of [value].
	 *
	 * If the value of [value] is null, returns [ifNull]. (defaults to [ItemStack.EMPTY])
	 */
	var asItem: ItemStack
		get() {
			if (value == null)
				return ifNull
			
			return convertToItem(value)
		}
		set(stack) {
			if (!acceptsItem(stack))
				return;
			
			// if tag is null, also sets to null
			value = convertFromItem(stack)
		}
	
	
	fun convertToItem(value: T?): ItemStack
	
	fun convertFromItem(stack: ItemStack): T?
	
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
	
	
	override fun convertToItem(value: T?): ItemStack {
		return ItemStack(defaultItem).apply {
			getOrCreateTag().writeToTag(value!!)
		}
	}
	
	override fun convertFromItem(stack: ItemStack): T? {
		return stack.tag?.readFromTag()
	}
	
	/**
	 * Deserialization function. Returns the value read from the ItemStack's root tag, or null if it's not present.
	 *
	 * Called in [asItem]'s setter.
	 */
	fun CompoundTag.readFromTag(): T?
	
	/**
	 * Serialization function. Puts the value into the ItemStack's root tag.
	 *
	 * Called in [asItem]'s getter.
	 */
	fun CompoundTag.writeToTag(value: T)
	
	/**
	 * Default implementation of [IItemRepresentable_Tag].
	 */
	class Impl<T>(
		override var value: T? = null,
		override val defaultItem: Item,
		val tagReader: CompoundTag.() -> T?,
		val tagWriter: CompoundTag.(T) -> Unit,
		override val ifNull: ItemStack = ItemStack.EMPTY
	) : IItemRepresentable_Tag<T> {
		override fun CompoundTag.readFromTag(): T? = tagReader()
		
		override fun CompoundTag.writeToTag(value: T) = tagWriter(value)
	}

	// Assignment Overloading
	@Suppress("UNUSED")
	fun assign(stack: ItemStack) {
		this.asItem = stack
	}
	@Suppress("UNUSED")
	fun assign(t: T?) {
		this.value = t
	}
}

fun IItemRepresentable_Tag<*>.dropItemInWorld(pLevel: Level, pPos: BlockPos): Boolean {
	if (this.value == null) {
		return false;
	}
	
	pLevel.dropItem(this.asItem, pPos.above().toVec3(), Vec3(0.0, 0.1, 0.0))
	this.value = null
	return true
}