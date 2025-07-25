@file:Suppress("NOTHING_TO_INLINE")

package btpos.mcmods.devutil.common.ext.vanilla.world

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceKey
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.SignalGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.phys.Vec3

inline fun LevelAccessor.getChunk(pos: ChunkPos): ChunkAccess = this.getChunk(pos.x, pos.z)


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
inline fun Level.modifyBlockAndUpdate(pos: BlockPos, updater: (BlockState) -> BlockState): Boolean {
	return this.setBlockAndUpdate(pos, updater(this.getBlockState(pos)))
}

/**
 * Apply a transformation to the blockstate at the given position.
 *
 * A macro for `level.setBlock(pos, level.getBlock(pos).something(), flags)`.
 *
 * @param updater Function that accepts the old state and returns the new.
 */
inline fun Level.modifyBlock(pos: BlockPos, flags: Int, updater: (BlockState) -> BlockState) = this.setBlock(pos, this.getBlockState(pos).let(updater), flags)

operator fun ResourceKey<Level>.contains(player: Player): Boolean {
	return player.level().dimension() == this
}

/**
 * Just a macro for `level.getSignal(pos.relative(dir), dir)`.
 *
 * @param ourPos The pos of this block
 * @param checkingDir The direction it's looking for signal from
 */
inline fun SignalGetter.getSignalReceivedFrom(ourPos: BlockPos, checkingDir: Direction) = this.getSignal(ourPos.relative(checkingDir), checkingDir)

@JvmInline
value class AfterSidedRun(val level: LevelReader) {
	inline val sidedResult: InteractionResult
		get() = InteractionResult.sidedSuccess(level.isClientSide)
}

inline fun LevelReader.runOnServer(action: () -> Unit): AfterSidedRun {
    if (!this.isClientSide)
        action()
    
	return AfterSidedRun(this)
}