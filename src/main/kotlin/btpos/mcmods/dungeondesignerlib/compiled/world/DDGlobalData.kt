package btpos.mcmods.dungeondesignerlib.compiled.world

import btpos.mcmods.devutil.common.macros.fastMapOf
import btpos.mcmods.dungeondesignerlib.builder.blocks.TileDungeonNexus
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.saveddata.SavedData
import java.util.UUID

//class DDGlobalData : SavedData() {
//	companion object {
//
//	}
//
//	/**
//	 * Each player on the server and their currently-selected DungeonNexus
//	 */
//	val currentDungeonNexus: MutableMap<UUID, BlockPos> = fastMapOf()
//	/*
//	Actually, I don't even think we need THIS.
//	As long as we have them make the bounds of the dungeon
//	(which they have to do anyway so we know how big to paste the thing),
//	 we can just find all blocks in there and build it that way.
//
//	 We don't need to link their player to a dungeon nexus when building or retain any references,
//	 we can just scan all blocks after.
//	 */
//
//	override fun save(tag: CompoundTag): CompoundTag {
//		TODO()
//	}
//}
