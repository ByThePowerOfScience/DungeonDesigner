@file:Suppress("NOTHING_TO_INLINE")

package btpos.mcmods.devutil.common.util

import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Component.literal
import net.minecraft.network.chat.MutableComponent

object ChatUtils {
	fun toComponent(pos: BlockPos): MutableComponent {
		return with(pos){ // TODO make it actually look nice, or use a library
			literal("[$x, $y, $z]").withStyle(ChatFormatting.YELLOW)
		}
	}
}

inline operator fun MutableComponent.plus(next: Component): MutableComponent = this.append(next)
inline operator fun MutableComponent.plus(next: String): MutableComponent = this.append(literal(next))