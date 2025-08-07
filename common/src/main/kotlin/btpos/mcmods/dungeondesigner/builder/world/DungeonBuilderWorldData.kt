package btpos.mcmods.dungeondesigner.builder.world

import btpos.mcmods.devutil.common.util.serialization.ICodecSerializable
import btpos.mcmods.devutil.common.structure.composition.IOnChange
import btpos.mcmods.dungeondesigner.common.redstone.WirelessRedstoneController
import btpos.mcmods.dungeondesigner.compiled.saveddata.FlagName
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.objects.Object2BooleanMap
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import net.minecraft.world.level.storage.DimensionDataStorage
import kotlin.collections.contains

val DimensionDataStorage.dungeonBuilderData: DungeonBuilderState
	get() = computeIfAbsent(DungeonBuilderWorldData.DATA_TYPE).state

class DungeonBuilderWorldData(val state: DungeonBuilderState = DungeonBuilderState()) : SavedData() {
//	constructor(tag: CompoundTag) : this(tag.readNbtSerializable(TAGKEY_STATE, DungeonBuilderState.CODEC))
	
	init {
		state.onChange = this::setDirty
	}
	
	companion object {
		const val TAGKEY_STATE = "state"
		
		val CODEC = RecordCodecBuilder.create {
			it.group(
					DungeonBuilderState.CODEC.fieldOf(TAGKEY_STATE).forGetter(DungeonBuilderWorldData::state)
			).apply(it, ::DungeonBuilderWorldData)
		}
		
		val DATA_TYPE: SavedDataType<DungeonBuilderWorldData> = SavedDataType("dungeon_designer_builder", { ctx -> DungeonBuilderWorldData() }, { ctx -> CODEC }, null)
	}
    
    
 
	
//	override fun save(pCompoundTag: CompoundTag): CompoundTag {
//		pCompoundTag.putNbtSerializable(TAGKEY_STATE, state)
//		return pCompoundTag
//	}
	
	
}
// TODO figure out how to propogate up the onChange method...
class DungeonBuilderState(
	/**
	 * Stores the value of each flag
	 */
	val flagStates: Object2BooleanMap<FlagName> = Object2BooleanOpenHashMap(),
	val redstoneHandler: WirelessRedstoneController = WirelessRedstoneController()
) : ICodecSerializable<DungeonBuilderState>, IOnChange {
    //region ICodecSerializable
    override fun codec() = CODEC
    //endregion
	
	override var onChange = {}
		set(value) {
			field = value
			redstoneHandler.onChange = value
		}
	
	
	//region Flags
	fun addFlag(name: FlagName) {
		if (name in flagStates)
			return
		
		flagStates.put(name, false)
		onChange()
	}
	
	fun removeFlag(name: FlagName) {
		if (name !in flagStates)
			return
		
		flagStates.removeBoolean(name)
		onChange()
	}
	
	fun setFlag(name: FlagName, value: Boolean) {
		val old = flagStates.put(name, value)
		
		if (old != value)
			onChange()
	}
	
	fun getFlag(name: FlagName): Boolean {
		return flagStates.getBoolean(name)
	}
	//endregion
	
	
	companion object {
		val CODEC = RecordCodecBuilder.create {
			it.group(
				Codec.unboundedMap(Codec.STRING, Codec.BOOL).fieldOf("flags").forGetter(DungeonBuilderState::flagStates),
				WirelessRedstoneController.CODEC.fieldOf("redstone_handler").forGetter(DungeonBuilderState::redstoneHandler)
			).apply(it) { flags, redstone_handler ->
				DungeonBuilderState(Object2BooleanOpenHashMap(flags), redstone_handler)
			}
		}
	}
}

