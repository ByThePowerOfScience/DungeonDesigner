package btpos.mcmods.dungeondesignerlib.builder.blocks.actors

import btpos.mcmods.devutil.common.ext.vanilla.data.getCompoundOrNull
import btpos.mcmods.devutil.common.ext.vanilla.data.getOrCreateCompound
import btpos.mcmods.dungeondesignerlib.builder.items.ItemTriggerVariable.Companion.TAGKEY_STATE
import btpos.mcmods.dungeondesignerlib.builder.items.ItemTriggerVariable.Companion.getOrCreateTriggerNbt
import btpos.mcmods.dungeondesignerlib.builder.nbt.TriggerBoundsTag
import btpos.mcmods.dungeondesignerlib.common.nbtadapters.DisplayNameGetter
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.phys.AABB
import org.junit.jupiter.api.assertAll
import kotlin.test.Test
import kotlin.test.assertEquals

class TileTriggerHolderTest {

}

// TODO unit test these during gametests
class TriggerVarItemConverterTest {
    fun `Able to read values from an incoming item's tag`() {
        // Expected values
        val testName = "bob marley"
        val firstPos = BlockPos(1,2,3); val secondPos = BlockPos(4,5,6)
        
        // Setup
        val sourceTag = CompoundTag().also {
            DisplayNameGetter(it).nameJson = testName
            getOrCreateTriggerNbt(it).let(::TriggerBoundsTag).run {
                first = firstPos
                second = secondPos
            }
        }
        
        val converter = TriggerVarItemConverter()
        with (converter) {
            sourceTag.readFromTag()
        }
        
        
        // Tests
        with (converter) {
            assertAll(
                { assertEquals(testName, name) },
                { assertEquals(AABB(firstPos, secondPos), value) },
            )
        }
    }
    
    fun `Able to write values to an outgoing item's tag`() {
        // Constants
        val testName = "bob marley"
        val firstPos = BlockPos(1,2,3); val secondPos = BlockPos(4,5,6)
        val expectedAABB = AABB(firstPos, secondPos)
        
        val expected = CompoundTag().also {
            DisplayNameGetter(it).nameJson = testName
            getOrCreateTriggerNbt(it).let(::TriggerBoundsTag).putAABB(expectedAABB)
        }
        val converter = TriggerVarItemConverter(expectedAABB, testName)
        
        // Action
        val actual = CompoundTag().also {
            with (converter) {
                it.writeToTag()
            }
        }
        
        
        // Tests
        assertEquals(expected, actual)
    }
}