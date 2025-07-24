package btpos.mcmods.dungeondesignerlib.builder.nbt

import btpos.mcmods.devutil.common.ext.vanilla.data.getBlockPos
import btpos.mcmods.devutil.common.ext.vanilla.data.getCompoundOrNull
import btpos.mcmods.devutil.common.ext.vanilla.data.getStringOrNull
import btpos.mcmods.devutil.common.ext.vanilla.data.setOrRemove
import btpos.mcmods.devutil.common.ext.vanilla.data.toCompoundTag
import btpos.mcmods.devutil.common.ext.vanilla.plus
import btpos.mcmods.devutil.common.macros.ChatUtils
import btpos.mcmods.devutil.common.util.serialization.Serialization.decodeTag
import btpos.mcmods.devutil.common.util.serialization.Serialization.encodeToTag
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.TAGKEY_SPAWNED_BY_FIGHT_CONTROLLER
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.NumericTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobSpawnType
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3
import java.util.UUID

sealed interface IEntitySpawnData {
	/**
	 * The spawn position of the entity.
	 */
	var pos: BlockPos?
	
	/**
	 * The X rotation of the spawned entity.
	 */
	var rotation: Float?
	
	/**
	 * The entity type to spawn. Copied from the template entity.
	 */
	var type: EntityType<*>?
	
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
	var nbt: CompoundTag?
	
	operator fun component1() = pos
	operator fun component2() = rotation
	operator fun component3() = type
	operator fun component4() = nbt
	
	
	/**
	 * Proper Java object for use in a BlockEntity
	 */
	data class ForTile(
		override var pos: BlockPos?,
		override var rotation: Float?,
		override var type: EntityType<*>?,
		override var nbt: CompoundTag? = null,
	) : IEntitySpawnData
	
	/**
	 * Structured access directly to item NBT.
	 */
	// I really want to use an interface here, but it also means the value class can't get this conversion for free...
	@JvmInline
	value class AsTag(val tag: CompoundTag) : IEntitySpawnData {
		companion object {
			const val SPAWN_POS = "pos"
			const val SPAWN_ROT = "rotation"
			const val ENT_TYPE = "ent_type"
			const val ENT_NBT = "ent_nbt"
		}
		
		override var pos: BlockPos?
			get() = tag.getBlockPos(SPAWN_POS)
			set(value) {
				tag.setOrRemove(SPAWN_POS, value) {
					put(SPAWN_POS, it.toCompoundTag())
				}
			}
		
		override var type: EntityType<*>?
			get() {
				return tag.get(ENT_TYPE)?.let { ForgeRegistries.ENTITY_TYPES.codec.decodeTag(it) }
			}
			set(value) {
				tag.setOrRemove(ENT_TYPE, value) {
					put(ENT_TYPE, ForgeRegistries.ENTITY_TYPES.codec.encodeToTag(it))
				}
			}
		
		override var nbt: CompoundTag?
			get() = tag.getCompoundOrNull(ENT_NBT)
			set(value) = tag.setOrRemove(ENT_NBT, value) { put(ENT_NBT, it) }
		
		override var rotation: Float?
			get() = (tag[SPAWN_ROT] as? NumericTag)?.asFloat
			set(value) {
				tag.setOrRemove(SPAWN_ROT, value) {
					putFloat(SPAWN_ROT, it)
				}
			}
		
		fun copyFrom(other: IEntitySpawnData) {
			pos = other.pos
			type = other.type
			nbt = other.nbt
			rotation = other.rotation
		}
		
		override fun toString(): String {
			return "Pos: $pos\nRot: $rotation\nType: $type\nNBT: ${nbt?.let(NbtUtils::prettyPrint)}"
		}
	}
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
	
	val consumer: (Entity) -> Unit = {
		it.deserializeNBT(nbt)
		it.persistentData.putBoolean(TAGKEY_SPAWNED_BY_FIGHT_CONTROLLER, true)
		if (rot != null)
			it.xRot = rot
		it.setPos(pos.above().toVec3())
		
	}
	
	val newEntity = type.create(level, null /* This param is entirely unused. idk why */, consumer, pos, MobSpawnType.MOB_SUMMONED, true, false) ?: return null
	
	if (level.tryAddFreshEntityWithPassengers(newEntity))
		return newEntity.uuid
	else
		return null
}

fun IEntitySpawnData.serialize(): CompoundTag {
	return when (this) {
		is IEntitySpawnData.AsTag -> this.tag
		else -> IEntitySpawnData.AsTag(CompoundTag()).also { it.copyFrom(this) }.tag
	}
}

fun IEntitySpawnData.toComponent(): Component {
	return with (ChatUtils) {
		-"Pos: " + pos.toComponent()[ChatFormatting.YELLOW] +
				"\nRotation: " + Component.literal(rotation.toString())[ChatFormatting.YELLOW] +
				"\nEntity Type: " + (-type?.let { ForgeRegistries.ENTITY_TYPES.getKey(it).toString() })[ChatFormatting.BLUE] +
				"\nEntity NBT: " + NbtUtils.toPrettyComponent(nbt ?: CompoundTag())
	}
}