package btpos.unittests.builder.blocks

import btpos.mcmods.devutil.common.util.serialization.Serialization
import btpos.mcmods.devutil.common.util.serialization.Serialization.encodeToTag
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.TileTriggerHolder.TriggerHolderState
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.phys.AABB
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals


class TriggerHolderStateTest {
	val bounds = AABB(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
	val id = UUID(123, 456)
	
	@Test
	fun serialize_toTag() {
		val state = TriggerHolderState(bounds, id)
		
		val expected = CompoundTag().also {
			it.put(TriggerHolderState.TAGKEY_BOUNDS, Serialization.CODEC_AABB_BLOCK.encodeToTag(bounds))
			it.put(TriggerHolderState.TAGKEY_PLACER, UUIDUtil.CODEC.encodeToTag(id))
		}
		
		val actual = CompoundTag().also {
			state.writeAsNbt(it)
		}
		
		assertEquals(expected, actual)
	}
	
	@Test
	fun serialize_fromTag() {
		val expectedState = TriggerHolderState(bounds, id)
		
		val tag = TriggerHolderState.CODEC.encodeToTag(expectedState) as CompoundTag
		
		val decodedState = TriggerHolderState().apply {
			populateFromNbt(tag)
		}
		
		assertEquals(expectedState.trigger, decodedState.trigger)
		assertEquals(expectedState.placer, decodedState.placer)
	}
}