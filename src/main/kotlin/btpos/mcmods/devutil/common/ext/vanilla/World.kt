@file:Suppress("NOTHING_TO_INLINE")

package btpos.mcmods.devutil.common.ext.vanilla

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

inline fun LevelAccessor.getChunk(pos: ChunkPos): ChunkAccess = this.getChunk(pos.x, pos.z)


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

fun AABB.getMinCorner(): Vec3 {
	return Vec3(minX, minY, minZ)
}
fun AABB.getMaxCorner(): Vec3 {
	return Vec3(maxX, maxY, maxZ)
}
fun AABB.getMinCornerBlock(): BlockPos {
	return BlockPos(minX.toInt(), minY.toInt(), minZ.toInt())
}
fun AABB.getMaxCornerBlock(): BlockPos {
	return BlockPos(maxX.toInt(), maxY.toInt(), maxZ.toInt())
}

/**
 * Macro to drop an itemstack in the world at the given position.
 */
fun Level.dropItem(item: ItemStack, pos: Vec3, velocity: Vec3? = null) {
	val ent = if (velocity != null) {
		ItemEntity(this, pos.x, pos.y, pos.z, item, velocity.x, velocity.y, velocity.z)
	} else {
		ItemEntity(this, pos.x, pos.y, pos.z, item)
	}
	
	this.addFreshEntity(ent)
}

/**
 * Apply a transformation to the blockstate at the given position.
 * Updates the block on both the client and server.
 *
 * A macro for `level.setBlockAndUpdate(pos, level.getBlock(pos).something())`.
 *
 * @param updater An inlined function that accepts the old state and returns the new.
 */
inline fun Level.changeBlockAndUpdate(pos: BlockPos, updater: (BlockState) -> BlockState): Boolean {
	return this.setBlockAndUpdate(pos, updater(this.getBlockState(pos)))
}

/**
 * Apply a transformation to the blockstate at the given position.
 *
 * A macro for `level.setBlock(pos, level.getBlock(pos).something(), flags)`.
 *
 * @param updater Function that accepts the old state and returns the new.
 */
inline fun Level.changeBlock(pos: BlockPos, flags: Int, updater: (BlockState) -> BlockState) = this.setBlock(pos, this.getBlockState(pos).let(updater), flags)

