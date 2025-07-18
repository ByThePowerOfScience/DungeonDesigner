package btpos.mcmods.dungeondesignerlib.builder.world

import btpos.mcmods.devutil.common.util.serialization.ICodecSerializable
import btpos.mcmods.devutil.common.util.serialization.putNbtSerializable
import btpos.mcmods.devutil.common.util.serialization.readNbtSerializable
import btpos.mcmods.dungeondesignerlib.compiled.saveddata.FlagName
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.objects.Object2BooleanMap
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.storage.DimensionDataStorage

val DimensionDataStorage.dungeonBuilderData: DungeonBuilderWorldData
	get() = computeIfAbsent(::DungeonBuilderWorldData, ::DungeonBuilderWorldData, "dungeon_designer_builder")

class DungeonBuilderWorldData(
	private val state: DungeonBuilderState = DungeonBuilderState()
) : SavedData() {
	constructor(tag: CompoundTag) : this(tag.readNbtSerializable(TAGKEY_STATE, DungeonBuilderState.CODEC))
	
	companion object {
		const val TAGKEY_STATE = "state"
	}
	
	
	fun addFlag(name: FlagName) {
		if (name in state.flagStates)
			return
		
		state.flagStates.put(name, false)
		setDirty()
	}
	
	fun removeFlag(name: FlagName) {
		if (name !in state.flagStates)
			return
		
		state.flagStates.removeBoolean(name)
		setDirty()
	}
	
	fun setFlag(name: FlagName, value: Boolean) {
		val old = state.flagStates.put(name, value)
		
		if (old != value)
			setDirty()
	}
	
	fun getFlag(name: FlagName): Boolean {
		return state.flagStates.getBoolean(name)
	}
	
	override fun save(pCompoundTag: CompoundTag): CompoundTag {
		pCompoundTag.putNbtSerializable(TAGKEY_STATE, state)
		return pCompoundTag
	}
}

class DungeonBuilderState(
	/**
	 * Stores the value of each flag
	 */
	val flagStates: Object2BooleanMap<FlagName> = Object2BooleanOpenHashMap(),
) : ICodecSerializable<DungeonBuilderState> {
	override fun codec() = CODEC
	
	override fun copyFrom(other: DungeonBuilderState) {
		this.flagStates.clear()
		this.flagStates.putAll(other.flagStates)
	}
	
	companion object {
		val CODEC = RecordCodecBuilder.create {
			it.group(
					Codec.unboundedMap(Codec.STRING, Codec.BOOL).fieldOf("flags").forGetter(DungeonBuilderState::flagStates)
			).apply(it) { map ->
				DungeonBuilderState(Object2BooleanOpenHashMap(map))
			}
		}
	}
}