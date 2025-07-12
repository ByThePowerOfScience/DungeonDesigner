@file:Suppress("NOTHING_TO_INLINE")

package btpos.mcmods.devutil.common.ext.vanilla

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.chunk.ChunkAccess

inline fun LevelAccessor.getChunk(pos: ChunkPos): ChunkAccess = this.getChunk(pos.x, pos.z)


inline fun BlockPos.toCompoundTag() = NbtUtils.writeBlockPos(this)

fun CompoundTag.asBlockPos(): BlockPos? {
	if (this.contains("X") && this.contains("Y") && this.contains("Z")) {
		return NbtUtils.readBlockPos(this)
	} else {
		return null
	}
}

fun CompoundTag.getBlockPos(key: String): BlockPos? {
	return this.getCompound(key).asBlockPos()
}