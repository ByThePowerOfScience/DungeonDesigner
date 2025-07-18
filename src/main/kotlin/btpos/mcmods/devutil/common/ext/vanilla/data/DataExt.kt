@file:Suppress("NOTHING_TO_INLINE")

package btpos.mcmods.devutil.common.ext.vanilla.data

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.Tag
import java.util.Optional

fun <T : Any> Codec<T>.optionalToNull() = this.xmap<Optional<T>>(Optional<T>::of) { it.orElse(null) }

inline operator fun CompoundTag.set(key: String, value: Tag) = this.put(key, value)
inline fun BlockPos.toCompoundTag() = NbtUtils.writeBlockPos(this)

fun CompoundTag.asBlockPos(): BlockPos? {
	if (this.contains("X", CompoundTag.TAG_INT.toInt())
	    && this.contains("Y", CompoundTag.TAG_INT.toInt())
	    && this.contains("Z", CompoundTag.TAG_INT.toInt()))
	{
		return NbtUtils.readBlockPos(this)
	} else {
		return null
	}
}

fun CompoundTag.getBlockPos(key: String): BlockPos? {
	return getCompoundOrNull(key)?.asBlockPos()
}

/**
 * Get a compound tag, *without* creating it if it doesn't exist.
 */
fun CompoundTag.getCompoundOrNull(key: String): CompoundTag? {
	if (!this.contains(key, CompoundTag.TAG_COMPOUND.toInt())) {
		return null
	}
	return this.getCompound(key)
}

fun CompoundTag.getStringOrNull(key: String): String? {
	if (!this.contains(key, CompoundTag.TAG_STRING.toInt())) {
		return null
	}
	return this.getString(key)
}