package btpos.mcmods.devutil.common.util.serialization

import com.mojang.serialization.Codec
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps

/**
 * Used on companion objects so I can just pass the class name of the thing to a function to deserialize them
 * @param T The type that the codec deserializes
 */
interface HasCodec<T> {
	val CODEC: Codec<T>
	
	fun decodeCompoundTag(tag: CompoundTag): T {
		return CODEC.decode(NbtOps.INSTANCE, tag).map { it.first }.getOrThrow(false, {})
	}
}
