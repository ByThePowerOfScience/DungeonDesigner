@file:Suppress("NOTHING_TO_INLINE")

package btpos.mcmods.devutil.common.ext.vanilla

import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder

inline fun <T : Any> InteractionResult.holder(held: T): InteractionResultHolder<T> = InteractionResultHolder(this, held)

