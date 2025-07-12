package btpos.mcmods.devutil.common.ext.vanilla.conversion

import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec3

fun Vec3i.toVec3() = Vec3(x.toDouble(), y.toDouble(), z.toDouble())
fun Vec3.toVec3i() = Vec3i(x.toInt(), y.toInt(), z.toInt())