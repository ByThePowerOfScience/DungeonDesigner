@file:Suppress("NOTHING_TO_INLINE")

package btpos.mcmods.devutil.common.ext.vanilla

import com.mojang.serialization.Codec
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import java.util.Optional

fun <T : Any> Codec<T>.optionalToNull() = this.xmap<Optional<T>>(Optional<T>::of) { it.orElse(null) }

inline operator fun CompoundTag.set(key: String, value: Tag) = this.put(key, value)