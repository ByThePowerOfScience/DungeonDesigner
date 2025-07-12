package btpos.mcmods.dungeondesignerlib.world

import btpos.mcmods.dungeondesignerlib.blocks.DungeonNexusInternalState
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData
import java.util.UUID

class DDGlobalData(
	val dungeonNexusDatas: MutableMap<UUID, DungeonNexusInternalState> = mutableMapOf()
) : SavedData() {
	companion object {
		val FACTORY = Factory<DDGlobalData>({ DDGlobalData() }, { CODEC.decode(NbtOps.INSTANCE, it).getOrThrow(false, {}).first }, TODO())
		
		val CODEC: Codec<DDGlobalData> = RecordCodecBuilder.create {
			it.group(
					Codec.unboundedMap(UUIDUtil.CODEC, DungeonNexusInternalState.CODEC).fieldOf("dungeon_nexus_data").forGetter(DDGlobalData::dungeonNexusDatas)
			).apply(it, ::DDGlobalData)
		}
	}
	
	// wait none of this is necessary, we can just store it as NBT in the block itself and then we move the block
	// maybe a map of UUID to blockpos?
	
	override fun save(tag: CompoundTag): CompoundTag {
		return CODEC.encode(this, NbtOps.INSTANCE, tag).map { it as CompoundTag }.getOrThrow(false, {})
	}
}

/**
 * Get our server level stuff in one place, idk if this is necessary but it's more concise
 */
val ServerLevel.ddStuff: DDServerLevelAccessor
	get() = DDServerLevelAccessor(this)


@JvmInline
value class DDServerLevelAccessor(internal val source: ServerLevel) {
	val worldData: DDGlobalData
		get() = source.server.overworld().dataStorage.computeIfAbsent(DDGlobalData.FACTORY, "dungeon_designer_lib")
}