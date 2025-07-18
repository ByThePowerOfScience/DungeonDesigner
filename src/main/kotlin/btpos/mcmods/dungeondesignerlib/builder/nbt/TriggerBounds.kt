package btpos.mcmods.dungeondesignerlib.builder.nbt

import btpos.mcmods.devutil.common.ext.vanilla.data.getBlockPos
import btpos.mcmods.devutil.common.ext.vanilla.data.toCompoundTag
import btpos.mcmods.devutil.common.util.serialization.Serialization
import btpos.mcmods.devutil.common.util.serialization.Serialization.encodeToTag
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.phys.AABB


/**
 * Wrapper giving managed structural access to a CompoundTag, because Items + Codecs = unfun.
 */
@JvmInline
value class TriggerBoundsTag(val tag: CompoundTag) {
	companion object {
		val CODEC = Serialization.CODEC_AABB_BLOCK
	}
	constructor(aabb: AABB) : this(CODEC.encodeToTag(aabb) as CompoundTag)
	
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