package btpos.test.dungeondesignerlib.itemnbtadapters

import btpos.mcmods.devutil.common.ext.vanilla.data.getOrCreateCompound
import btpos.mcmods.dungeondesignerlib.common.nbtadapters.DisplayNameGetter
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack.TAG_DISPLAY
import net.minecraft.world.item.ItemStack.TAG_DISPLAY_NAME
import kotlin.test.Test
import kotlin.test.assertEquals


class DisplayNameGetterTest {
    @Test
    fun readFromTag() {
        val expectedName = "jorge (pronounced horge)"
        
        val actual = CompoundTag().apply {
            getOrCreateCompound(TAG_DISPLAY).putString(TAG_DISPLAY_NAME, expectedName)
        }.let(::DisplayNameGetter).nameJson
        
        assertEquals(expectedName, actual)
    }
    
    @Test
    fun writeToTag() {
        val theName = "jorge (pronounced horge)"
        
        val expected = CompoundTag().apply {
            getOrCreateCompound(TAG_DISPLAY).putString(TAG_DISPLAY_NAME, theName)
        }
        
        val actual = DisplayNameGetter(CompoundTag()).apply { nameJson = theName }.tag
        
        assertEquals(expected, actual)
    }
}