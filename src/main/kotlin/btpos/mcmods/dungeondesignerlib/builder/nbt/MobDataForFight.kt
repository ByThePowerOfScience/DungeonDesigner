package btpos.mcmods.dungeondesignerlib.builder.nbt

import btpos.mcmods.devutil.common.ext.vanilla.data.getBlockPos
import btpos.mcmods.devutil.common.ext.vanilla.data.getCompoundOrNull
import btpos.mcmods.devutil.common.ext.vanilla.data.toCompoundTag
import btpos.mcmods.devutil.common.util.serialization.Serialization.decodeTag
import btpos.mcmods.devutil.common.util.serialization.Serialization.encodeToTag
import btpos.mcmods.dungeondesignerlib.builder.nbt.PipetteData.NbtAdapter
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries

sealed interface IMobData {
	var type: EntityType<out LivingEntity>?
	var nbt: CompoundTag?
	
	operator fun component1() = type
	operator fun component2() = nbt
}

fun IMobData.toNbt(): CompoundTag {
	val adapter = this as? MobData.NbtAdapter ?: MobData.NbtAdapter(CompoundTag()).also {
		it.type = this.type
		it.nbt = this.nbt
	}
	return adapter.tag
}

fun IMobData.toObject(): MobData {
	return this as? MobData ?: MobData(this.type, this.nbt)
}

data class MobData(override var type: EntityType<out LivingEntity>?, override var nbt: CompoundTag? = null) : IMobData {
	@JvmInline // Unsure if there's more boxing and unboxing by trying to use a value class like this than just using a regular class...
	value class NbtAdapter(val tag: CompoundTag) : IMobData {
		override var type: EntityType<out LivingEntity>? // key: "type"
			get() {
				@Suppress("UNCHECKED_CAST")
				return tag.get("type")?.let {
					ForgeRegistries.ENTITY_TYPES.codec.decodeTag(it)
				}?.takeIf {
					it.baseClass.isAssignableFrom(LivingEntity::class.java)
				} as EntityType<out LivingEntity>?
			}
			set(value) {
				if (value == null)
					tag.remove("type")
				else
					tag.put("type", ForgeRegistries.ENTITY_TYPES.codec.encodeToTag(value))
			}
		
		override var nbt: CompoundTag?
			get() = tag.getCompoundOrNull("data")
			set(value) {
				if (value == null)
					tag.remove("data")
				else
					tag.put("data", value)
			}
	}
}

sealed interface IPipetteData {
	var mob: IMobData?
	var spawnPos: BlockPos?
	
	operator fun component1() = mob
	operator fun component2() = spawnPos
}

//region Pipette NBT Keys
private const val TAGKEY_MOBDATA = "mob_data"
private const val TAGKEY_SPAWNPOS = "spawn_pos"
//endregion

data class PipetteData(override var mob: IMobData?, override var spawnPos: BlockPos?) : IPipetteData {
	@JvmInline
	value class NbtAdapter(val tag: CompoundTag) : IPipetteData {
		override var mob: IMobData?
			get() {
				return tag.getCompoundOrNull(TAGKEY_MOBDATA)?.let(MobData::NbtAdapter)
			}
			set(value) {
				if (value == null)
					tag.remove(TAGKEY_MOBDATA)
				else
					tag.put(TAGKEY_MOBDATA, value.toNbt())
			}
		
		override var spawnPos: BlockPos?
			get() = tag.getBlockPos(TAGKEY_SPAWNPOS)
			set(value) {
				if (value == null)
					tag.remove(TAGKEY_SPAWNPOS)
				else
					tag.put(TAGKEY_SPAWNPOS, value.toCompoundTag())
			}
	}
}