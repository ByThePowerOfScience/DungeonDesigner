package btpos.mcmods.devutil.common.util.serialization

import btpos.mcmods.devutil.common.ext.vanilla.getMaxCorner
import btpos.mcmods.devutil.common.ext.vanilla.getMaxCornerBlock
import btpos.mcmods.devutil.common.ext.vanilla.getMinCorner
import btpos.mcmods.devutil.common.ext.vanilla.getMinCornerBlock
import com.mojang.logging.LogUtils
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.Tag
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.apache.commons.logging.LogFactory
import org.slf4j.LoggerFactory
import java.util.logging.LogManager

private val LOGGER = LoggerFactory.getLogger("btpos Serialization")

object Serialization {
	val CODEC_AABB = RecordCodecBuilder.create {
		it.group(
				Vec3.CODEC.fieldOf("first").forGetter { aabb: AABB -> aabb.getMinCorner() },
				Vec3.CODEC.fieldOf("second").forGetter { aabb: AABB -> aabb.getMaxCorner() }
		).apply(it, ::AABB)
	}
	val CODEC_AABB_BLOCK = RecordCodecBuilder.create {
		it.group(
				BlockPos.CODEC.fieldOf("first").forGetter { aabb: AABB -> aabb.getMinCornerBlock() },
				BlockPos.CODEC.fieldOf("second").forGetter { aabb: AABB -> aabb.getMaxCornerBlock() }
		).apply(it, ::AABB)
	}
	
	fun <T> Codec<T>.decodeTag(tag: Tag): T {
		return this.decode(NbtOps.INSTANCE, tag).getOrThrow(false, {LOGGER.error("Fail codec decode: {}", it)}).first
	}
	
	fun <T> Codec<T>.encodeToTag(item: T): Tag {
		return this.encodeStart(NbtOps.INSTANCE, item).getOrThrow(false, { LOGGER.error("Fail codec encode: {}", it) })
	}
	
	// TODO figure out if the codec is causing performance issues
}