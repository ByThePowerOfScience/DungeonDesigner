package btpos.mcmods.devutil.common.ext.vanilla.world

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

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