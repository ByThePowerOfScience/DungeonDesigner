package btpos.mcmods.devutil.forge.ext.capability

import net.minecraftforge.common.util.LazyOptional

fun <T> LazyOptional<T>.getOrNull(): T? {
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    return this.orElse(null)
}