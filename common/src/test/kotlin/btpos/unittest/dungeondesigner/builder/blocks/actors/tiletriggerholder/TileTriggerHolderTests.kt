package btpos.unittest.dungeondesigner.builder.blocks.actors.tiletriggerholder

import btpos.mcmods.devutil.common.ext.vanilla.stack
import btpos.mcmods.devutil.common.ext.vanilla.world.AABB
import btpos.mcmods.devutil.common.ext.vanilla.world.BlockInclusiveAABB.Companion.toBlockInclusive
import btpos.mcmods.devutil.common.macros.mto
import btpos.mcmods.dungeondesigner.builder.blocks.actors.TriggerVarItemConverter
import btpos.mcmods.dungeondesigner.builder.items.ItemTriggerVariable
import btpos.mcmods.dungeondesigner.common.nbtadapters.getDisplayName
import btpos.mcmods.dungeondesigner.common.nbtadapters.setDisplayName
import btpos.mcmods.dungeondesigner.registry.ModItems
import btpos.unittest.PlatformTestRunner
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.reflect.KMutableProperty
import kotlin.reflect.jvm.isAccessible
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(PlatformTestRunner::class)
class TriggerVarItemConverterTest {
	/**
	 * access the private setter, which was actually inlined by K2 to direct field access
	 */
	private val nameSetter = TriggerVarItemConverter::class.java.getDeclaredField("name").apply {
		isAccessible = true
	}
	private fun TriggerVarItemConverter.setName(name: String) {
		nameSetter.set(this, name)
	}
	
    @Test
    fun `Able to read values from an incoming item`(server: MinecraftServer) {
        // Expected values
        val testName = "bob marley"
        val firstPos = BlockPos(1, 2, 3); val secondPos = BlockPos(4, 5, 6)

        // Setup
	    val sourceItem = ModItems.TRIGGER_ITEM.stack().apply {
		    setDisplayName(this, testName)
		    ItemTriggerVariable.modifyOrCreateData(this) {
				first = firstPos
			    second = secondPos
		    }
	    }
	    
	    
	    val translator = TriggerVarItemConverter()
	    translator.asItem = sourceItem
	    
	    assertAll(
			    { assertEquals(testName, translator.name) },
			    { assertEquals(firstPos mto secondPos, translator.value) }
		)
    }

    @Test
    fun `Able to write values to an outgoing item`(server: MinecraftServer) {
        // Constants
        val testName = "bob marley"
        val firstPos = BlockPos(1,2,3); val secondPos = BlockPos(4,5,6)
	    
	    val actualItem = TriggerVarItemConverter().apply {
			setName(testName)
		    value = firstPos mto secondPos
	    }.asItem
	    
	    val data = ItemTriggerVariable.getData(actualItem).also {
		    assertNotNull(it) { "No data on actual item!" }
	    }!!
	    assertAll(
			    { assertEquals(testName, getDisplayName(actualItem)) },
			    { assertEquals(firstPos, data.first) },
			    { assertEquals(secondPos, data.second) },
		)
    }
	
	@Test
	fun `Supplies correct AABB`() {
		val firstPos = BlockPos(1,2,3); val secondPos = BlockPos(4,5,6)
		
		val expected = AABB(firstPos, secondPos).toBlockInclusive().bb
		
		val actual = TriggerVarItemConverter().apply {
			value = firstPos mto secondPos
		}.cachedInclusiveAABB!!.bb
		
		assertEquals(expected, actual)
	}
}