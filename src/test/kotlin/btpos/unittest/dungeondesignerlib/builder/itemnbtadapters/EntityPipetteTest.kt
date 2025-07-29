package btpos.test.dungeondesignerlib.itemnbtadapters

import btpos.mcmods.devutil.common.ext.vanilla.data.toCompoundTag
import btpos.mcmods.dungeondesignerlib.builder.nbt.IEntitySpawnData.AsTag
import btpos.mcmods.dungeondesignerlib.builder.nbt.IEntitySpawnData.AsTag.Companion.ENT_NBT
import btpos.mcmods.dungeondesignerlib.builder.nbt.IEntitySpawnData.AsTag.Companion.ENT_TYPE
import btpos.mcmods.dungeondesignerlib.builder.nbt.IEntitySpawnData.AsTag.Companion.SPAWN_POS
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import org.junit.jupiter.api.assertAll
import kotlin.test.Test
import kotlin.test.assertEquals

class EntityPipetteTest {
    @Test
    fun deserialize() {
        val pos = BlockPos(0, 0, 0)
        val rot = 1.0f
        val nbt = CompoundTag().apply {
            putString("name", "jacob")
        }
        
        val testTag = CompoundTag().apply {
            put(SPAWN_POS, pos.toCompoundTag())
            putFloat(AsTag.SPAWN_ROT, rot)
            putString(ENT_TYPE, "minecraft:zombie") // I can't test the entity type since it needs the game to be running
            put(ENT_NBT, nbt)
        }
        
        val wrapped = AsTag(testTag)
        
        assertAll(
            { assertEquals(pos, wrapped.pos, "Position") },
            { assertEquals(rot, wrapped.rotation, "Rotation") },
            { assertEquals(nbt, wrapped.nbt, "NBT") },
        )
        // TODO figure out how to test the entity type...
    }
    
    @Test
    fun serialize() {
        val pos = BlockPos(0, 0, 0)
        val rot = 1.0f
        val nbt = CompoundTag().apply {
            putString("name", "jacob")
        }
        
        val expected = CompoundTag().apply {
            put(SPAWN_POS, pos.toCompoundTag())
            putFloat(AsTag.SPAWN_ROT, rot)
//            putString(ENT_TYPE, "minecraft:zombie") // I can't test the entity type since it needs the game to be running
            put(ENT_NBT, nbt)
        }
        
        val actual = AsTag(CompoundTag()).apply {
            this.pos = pos
            this.rotation = rot
            this.nbt = nbt
        }.tag
        
        assertEquals(expected, actual)
    }
}