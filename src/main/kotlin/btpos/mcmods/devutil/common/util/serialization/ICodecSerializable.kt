package btpos.mcmods.devutil.common.util.serialization

import btpos.mcmods.devutil.common.ext.vanilla.destructuring.component1
import btpos.mcmods.devutil.common.ext.vanilla.destructuring.component2
import btpos.mcmods.devutil.common.ext.vanilla.set
import btpos.mcmods.devutil.common.util.IReverseCloneable
import btpos.mcmods.devutil.common.util.serialization.Serialization.decodeTag
import btpos.mcmods.devutil.common.util.serialization.Serialization.encodeToTag
import btpos.mcmods.dungeondesignerlib.LOGGER
import com.mojang.serialization.Codec
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import org.slf4j.LoggerFactory
import kotlin.jvm.optionals.getOrNull

private val LOGGER = LoggerFactory.getLogger("btpos - ICodecSerializable")

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
//			TODO("Handle partial result")
			res.map { (partial, _) ->
				copyFrom(partial)
			}
		}
	}
	
	override fun writeAsNbt(tag: CompoundTag) {
		val newtag = codec().encode(self(), NbtOps.INSTANCE, null).resultOrPartial(LOGGER::error).getOrNull() as? CompoundTag
		if (newtag != null) {
			newtag.allKeys.forEach {
				tag[it] = newtag[it]!!
			}
		}
	}
}