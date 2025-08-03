@file:OptIn(ExperimentalContracts::class)

package btpos.mcmods.devutil.common.ext.vanilla.world

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.jetbrains.annotations.Contract
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Applies the transformer function to the entity's deltaMovement, and sets the entity's deltaMovement to the result.
 *
 * @see Entity.getDeltaMovement
 * @see Entity.setDeltaMovement
 */
inline fun Entity.modifyDeltaMovement(transformer: (Vec3) -> Vec3) {
	contract {
		callsInPlace(transformer, InvocationKind.EXACTLY_ONCE)
	}
	
	deltaMovement = transformer(deltaMovement)
}

/**
 * Returns `this` if its wrapped type is or is a subclass of `T`, else null.
 */
@Suppress("UNCHECKED_CAST") @Contract(pure=true)
inline fun <reified T : Entity> EntityType<*>.cast(level: Level): EntityType<T>? {
	if (this.create(level) is T) { // I hate this, but getBaseClass just returns Entity and not the actual T class
		return this as EntityType<T>
	} else {
		return null
	}
}

/**
 * Returns self if this EntityType wraps a [LivingEntity], else null.
 */
fun EntityType<*>.checkLivingEntity(level: Level): EntityType<out LivingEntity>? = this.cast(level)