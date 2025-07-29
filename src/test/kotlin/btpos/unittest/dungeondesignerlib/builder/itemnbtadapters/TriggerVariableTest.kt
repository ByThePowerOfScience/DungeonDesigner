package btpos.test.dungeondesignerlib.itemnbtadapters

import btpos.mcmods.devutil.common.ext.vanilla.data.toCompoundTag
import btpos.mcmods.dungeondesignerlib.builder.items.NbtAdapter
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import org.junit.jupiter.api.assertAll
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @see NbtAdapter
 */
class TriggerVariableTest {
    @Test
    fun readExistingTag() {
        val (firstPos, secondPos) = BlockPos(1, 2, 3) to BlockPos(4, 5, 6)
        
        val startingTag = CompoundTag().apply {
            put("first", firstPos.toCompoundTag())
            put("second", secondPos.toCompoundTag())
        }
        
        val data = NbtAdapter(startingTag)
        
        assertAll(
            { assertEquals(firstPos, data.first) },
            { assertEquals(secondPos, data.second) },
        )
    }
    
    @Test
    fun encodeToTag() {
        val (firstPos, secondPos) = BlockPos(1, 2, 3) to BlockPos(4, 5, 6)
        
        val expected = CompoundTag().apply {
            put("first", firstPos.toCompoundTag())
            put("second", secondPos.toCompoundTag())
        }
        
        val actual = NbtAdapter(CompoundTag()).apply {
            first = firstPos
            second = secondPos
        }.tag
        
        assertEquals(expected, actual)
    }
}