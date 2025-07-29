package btpos.test.dungeondesignerlib.itemnbtadapters

import btpos.mcmods.devutil.common.ext.vanilla.data.toCompoundTag
import btpos.mcmods.dungeondesignerlib.builder.redstone.items.ItemRemoteLinker
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import kotlin.test.Test
import kotlin.test.assertEquals

typealias Adapter = ItemRemoteLinker.NbtAdapter

class RemoteLinkerTest {
    @Test
    fun readExistingTag() {
        val testpos = BlockPos(0, 45, 313)
        
        val testTag = CompoundTag().apply {
            put("target", testpos.toCompoundTag())
        }
        
        assertEquals(testpos, Adapter(testTag).target)
    }
    
    @Test
    fun encodeToTag() {
        val testpos = BlockPos(0, 45, 313)
        
        val expected = CompoundTag().apply {
            put("target", testpos.toCompoundTag())
        }
        
        val actual = Adapter(CompoundTag()).apply {
            target = testpos
        }.tag
        
        assertEquals(expected, actual)
    }
}