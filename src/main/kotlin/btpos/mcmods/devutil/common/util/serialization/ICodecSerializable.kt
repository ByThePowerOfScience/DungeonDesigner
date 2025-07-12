package btpos.mcmods.devutil.common.util.serialization

import btpos.mcmods.devutil.common.ext.vanilla.destructuring.component1
import btpos.mcmods.devutil.common.ext.vanilla.destructuring.component2
import btpos.mcmods.devutil.common.util.IReverseCloneable
import com.mojang.serialization.Codec
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps

/**
 * Platform-agnostic NBT serialization helper using codecs.
 * Encodes/decodes the data of `this` to and from a [CompoundTag] using the [Codec] provided with [codec].
 */
interface ICodecSerializable<SELF> : IReverseCloneable<SELF>, INbtSerializable
		where SELF : IReverseCloneable<SELF>,
		      SELF : ICodecSerializable<SELF>
{
	
	@Suppress("UNCHECKED_CAST")
	private fun self(): SELF = this as SELF
	
	fun codec(): Codec<SELF>
	
	override fun populateFromNbt(tag: CompoundTag) {
		codec().decode(NbtOps.INSTANCE, tag).get().ifLeft { (made, _) ->
			copyFrom(made)
		}.ifRight { res ->
			// a partial result is like in a list where a few elements got decoded and then one of them didn't decode properly
			TODO("Handle partial result")
		}
	}
	
	override fun writeAsNbt(tag: CompoundTag) {
		codec().encode(self(), NbtOps.INSTANCE, tag)
	}
}