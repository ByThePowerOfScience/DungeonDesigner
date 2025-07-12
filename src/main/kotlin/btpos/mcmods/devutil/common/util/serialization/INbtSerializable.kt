package btpos.mcmods.devutil.common.util.serialization

import net.minecraft.nbt.CompoundTag

interface INbtSerializable {
	fun populateFromNbt(tag: CompoundTag)
	fun writeAsNbt(tag: CompoundTag)
}

fun CompoundTag.putNbtSerializable(key: String, serializable: INbtSerializable) {
	val tag = CompoundTag()
	serializable.writeAsNbt(tag)
	this.put(key, tag)
}

/**
 * Reads the tag and populates an existing item with its state.
 */
fun CompoundTag.readNbtSerializable(key: String, serializable: INbtSerializable) {
	serializable.populateFromNbt(this.getCompound(key))
}

/**
 * Creates a new instance of the object the tag represents.
 */
fun <T: ICodecSerializable<T>> CompoundTag.readNbtSerializable(key: String, codec: HasCodec<T>): T {
	return codec.decodeCompoundTag(this.getCompound(key))
}