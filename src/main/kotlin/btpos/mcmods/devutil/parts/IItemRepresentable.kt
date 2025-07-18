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
 * Wraps some value and provides functions converting it to and from an [net.minecraft.world.item.ItemStack].
 */
@KotlinAssignmentOverloadTarget
interface IItemRepresentable<T> {
	/**
	 * This will only perform ItemStack deserialization if the stack wraps this item.
	 */
	val acceptedItem: Item
	
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
			
			return ItemStack(acceptedItem).apply {
				getOrCreateTag().writeToTag(value!!)
			}
		}
		set(stack) {
			if (stack.item != acceptedItem)
				return;
			
			// if tag is null, also sets to null
			value = stack.tag?.readFromTag()
		}
	
	/**
	 * Deserialization function. Reads the value from the ItemStack's tag, or null if it's not present.
	 * Called in [asItem]'s setter.
	 */
	fun CompoundTag.readFromTag(): T?
	
	/**
	 * Serialization function. Puts the value into the ItemStack's tag.
	 * Called in [asItem]'s getter.
	 */
	fun CompoundTag.writeToTag(value: T)
	
	/**
	 * Default implementation of [IItemRepresentable].
	 */
	class Impl<T>(
		override var value: T? = null,
		override val acceptedItem: Item,
		val tagReader: CompoundTag.() -> T?,
		val tagWriter: CompoundTag.(T) -> Unit,
		override val ifNull: ItemStack = ItemStack.EMPTY
	) : IItemRepresentable<T> {
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

fun IItemRepresentable<*>.dropItemInWorld(pLevel: Level, pPos: BlockPos): Boolean {
	if (this.value == null) {
		return false;
	}
	
	pLevel.dropItem(this.asItem, pPos.above().toVec3(), Vec3(0.0, 0.1, 0.0))
	this.value = null
	return true
}