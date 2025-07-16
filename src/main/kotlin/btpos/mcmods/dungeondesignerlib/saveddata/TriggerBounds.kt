package btpos.mcmods.dungeondesignerlib.saveddata

import btpos.mcmods.devutil.common.ext.vanilla.getBlockPos
import btpos.mcmods.devutil.common.ext.vanilla.getMaxCornerBlock
import btpos.mcmods.devutil.common.ext.vanilla.getMinCornerBlock
import btpos.mcmods.devutil.common.ext.vanilla.toCompoundTag
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.phys.AABB


/**
 * Wrapper giving managed structural access to a CompoundTag, because Items + Codecs = unfun.
 */
@JvmInline
value class TriggerBoundsTag(val tag: CompoundTag) {
	constructor(aabb: AABB) : this(CompoundTag().apply {
		put("first", aabb.getMinCornerBlock().toCompoundTag())
		put("second", aabb.getMaxCornerBlock().toCompoundTag())
	})
	
	var first: BlockPos?
		get() = tag.getBlockPos("first")
		set(value) {
			if (value == null)
				tag.remove("first")
			else
				tag.put("first", value.toCompoundTag())
		}
	
	var second: BlockPos?
		get() = tag.getBlockPos("second")
		set(value) {
			if (value == null)
				tag.remove("second")
			else
				tag.put("second", value.toCompoundTag())
		}
	
	fun toAABB(): AABB? {
		return if (first == null || second == null) null else AABB(first!!, second!!)
	}
}