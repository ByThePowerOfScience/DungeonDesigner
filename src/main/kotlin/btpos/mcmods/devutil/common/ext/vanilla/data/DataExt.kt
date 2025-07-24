@file:Suppress("NOTHING_TO_INLINE")

package btpos.mcmods.devutil.common.ext.vanilla.data

import btpos.mcmods.devutil.common.ext.java.asOptional
import btpos.mcmods.devutil.common.ext.java.cast
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.Tag
import java.util.Objects
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

//region Codecs
fun <T> MapCodec<Optional<T>>.optionalToNull(): MapCodec<T?> {
	return this.xmap<T?>({ it.orElse(null) }, { Optional.ofNullable(it).cast() })
}


fun <T : Any> Codec<T>.nullableFieldOf(name: String): MapCodec<T?> {
	return this.optionalFieldOf(name).optionalToNull()
}


/**
 * WHY does DFU throw an NPE when you put null as the default value???  Sometimes you just want null to be the default!!!
 * You can't even use [Minecraft's idiom][optionalToNull].  If the value isn't present, it'll throw a NPE.
 *
 * I wonder if Mojang knows about this?
 *
 * Anyway, this is the best I could do: at least make it where I didn't have to use manual getters each time and could just use property reference syntax.
 */
inline fun <ENCL, T> MapCodec<Optional<T>>.forNullableGetter(crossinline getter: (ENCL) -> T?): RecordCodecBuilder<ENCL, Optional<T>> {
    return this.forGetter<ENCL> { encl -> Optional.ofNullable(getter(encl)).cast() }
}
//endregion

inline operator fun CompoundTag.set(key: String, value: Tag) = this.put(key, value)



//region BlockPos
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
//endregion

//region Get null instead of default
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

fun CompoundTag.getIntOrNull(key: String): Int? {
	if (!this.contains(key, CompoundTag.TAG_INT.toInt())) {
		return null
	}
	return this.getInt(key)
}
//endregion

fun CompoundTag.getOrCreateCompound(key: String): CompoundTag {
	if (!this.contains(key, CompoundTag.TAG_COMPOUND.toInt())) {
		return CompoundTag().also {
			this.put(key, it)
		}
	}
	return this.get(key)!! as CompoundTag
}

/**
 * Macro for "if the value is null, remove the key, else set it using the function
 */
inline fun <T> CompoundTag.setOrRemove(key: String, value: T?, setter: CompoundTag.(T) -> Unit) {
	if (value == null)
		this.remove(key)
	else
		this.setter(value)
}