package btpos.mcmods.dungeondesignerlib

import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB

object WorldUtils {
	fun isPlayerInBoundingBox(aabb: AABB, level: Level): Boolean {
		return level.players().any {
			it.position() in aabb
		}
	}
}