package btpos.mcmods.dungeondesigner.builder.nbt

import btpos.mcmods.devutil.common.ext.vanilla.data.nullSafeFieldOf
import btpos.mcmods.devutil.common.ext.vanilla.plus
import btpos.mcmods.devutil.common.kfflib.forge.vectorutil.v3d.toVec3
import btpos.mcmods.devutil.common.macros.ChatUtils
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.component.CustomData
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

sealed interface IEntitySpawnData {
	/**
	 * The spawn position of the entity.
	 */
	val pos: BlockPos?
	
	/**
	 * The X rotation of the spawned entity.
	 */
	val rotation: Float?
	
	/**
	 * The entity type to spawn. Copied from the template entity.
	 */
	val type: EntityType<*>?
	
	/**
	 * The NBT to give the entity when spawning it, e.g. inventory, weapons, max health, potion effects, etc.
	 *
	 * Copied directly from the template entity, minus these fields:
	 * - UUID
	 * - Pos
	 * - Dimension
	 *
	 * @see net.minecraft.client.KeyboardHandler.copyCreateEntityCommand
	 */
	val nbt: CompoundTag?
	
	operator fun component1() = pos
	operator fun component2() = rotation
	operator fun component3() = type
	operator fun component4() = nbt
	
	
	data class Mutable(
		override var pos: BlockPos? = null,
		override var rotation: Float? = null,
		override var type: EntityType<*>? = null,
		override var nbt: CompoundTag? = null
	) : IEntitySpawnData {
		constructor(other: IEntitySpawnData) : this(other.pos, other.rotation, other.type, other.nbt)
	}
	
	data class Impl(
		override val pos: BlockPos?,
		override val rotation: Float?,
		override val type: EntityType<*>?,
		override val nbt: CompoundTag? = null,
	) : IEntitySpawnData
	
	
	companion object {
		val CODEC: Codec<IEntitySpawnData> = RecordCodecBuilder.create {
			it.group(
					BlockPos.CODEC.nullSafeFieldOf("pos", IEntitySpawnData::pos),
					Codec.FLOAT.nullSafeFieldOf("rot", IEntitySpawnData::rotation),
					BuiltInRegistries.ENTITY_TYPE.byNameCodec().nullSafeFieldOf("type", IEntitySpawnData::type),
					CompoundTag.CODEC.nullSafeFieldOf("nbt", IEntitySpawnData::nbt),
			).apply(it) { i, j, k, l ->
				Impl(i.getOrNull(), j.getOrNull(), k.getOrNull(), l.getOrNull())
			}
		}
	}
}

typealias MobSpawnType = EntitySpawnReason

/**
 * Put into entities' persistentdata to say they shouldn't be saved to the chunk.
 * @see btpos.mcmods.dungeondesigner.mixin.MArenaDespawnOnUnload
 */
const val TAGKEY_TEMPORARY_MOB = "dungeondesigner:nosavemob"

fun isMobTemporary(entity: Entity): Boolean {
	val data = entity.get(DataComponents.CUSTOM_DATA) ?: return false
	return data.contains(TAGKEY_TEMPORARY_MOB)
}

fun setMobTemporary(entity: Entity) {
	val tag = entity.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: CompoundTag()
	tag.putBoolean(TAGKEY_TEMPORARY_MOB, true)
	entity.setComponent(DataComponents.CUSTOM_DATA, CustomData.of(tag))
}

/**
 * Attempts to spawn the entity made by this data in the world. Entities spawned will not be saved to chunk NBT.
 * @return The UUID of the spawned entity if spawning was successful, else null.
 */
fun IEntitySpawnData.trySpawnEntity(level: ServerLevel): UUID? {
	val (pos, rot, type, nbt) = this
	if (pos == null || type == null) {
		return null
	}
	
	val tag = (nbt ?: CompoundTag()).apply {
		putString("id", type.`arch$holder`().registeredName)
	}
	
	val newEntity = EntityType.loadEntityRecursive(tag, level, MobSpawnType.MOB_SUMMONED) {
		it.snapTo(pos.toVec3(), rot ?: 0f, 0f)
		setMobTemporary(it)
		it
	} ?: return null
	
	if (level.tryAddFreshEntityWithPassengers(newEntity))
		return newEntity.uuid
	else
		return null
}

fun IEntitySpawnData.toComponent(): Component {
	return with (ChatUtils) {
		-"Pos: " + pos.toComponent()[ChatFormatting.YELLOW] +
		"\nRotation: " + Component.literal(rotation.toString())[ChatFormatting.YELLOW] +
		"\nEntity Type: " + (-type?.let { BuiltInRegistries.ENTITY_TYPE.getKey(it).toString() })[ChatFormatting.BLUE] +
		"\nEntity NBT: " + NbtUtils.toPrettyComponent(nbt ?: CompoundTag())
	}
}