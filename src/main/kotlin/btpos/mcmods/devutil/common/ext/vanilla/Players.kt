package btpos.mcmods.devutil.common.ext.vanilla

import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

operator fun ResourceKey<Level>.contains(player: Player): Boolean {
	return player.level().dimension() == this
}