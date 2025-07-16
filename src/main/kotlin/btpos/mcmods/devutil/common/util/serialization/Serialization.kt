package btpos.mcmods.devutil.common.util.serialization

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.Tag
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

object Serialization {
	val CODEC_AABB = RecordCodecBuilder.create {
		it.group(
				Vec3.CODEC.fieldOf("first").forGetter { aabb: AABB -> Vec3(aabb.minX, aabb.minY, aabb.minZ) },
				Vec3.CODEC.fieldOf("second").forGetter { aabb: AABB -> Vec3(aabb.maxX, aabb.maxY, aabb.maxZ) }
		).apply(it, ::AABB)
	}
	
	fun <T> Codec<T>.decodeTag(tag: Tag): T {
		return this.decode(NbtOps.INSTANCE, tag).getOrThrow(false, {}).first
	}
	
	fun <T> Codec<T>.encodeToTag(item: T): Tag {
		return this.encodeStart(NbtOps.INSTANCE, item).getOrThrow(false, {})
	}
	
	// TODO figure out if the codec is causing performance issues
}