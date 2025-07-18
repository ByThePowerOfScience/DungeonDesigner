package btpos.unittests.builder.blocks

import btpos.mcmods.devutil.common.util.serialization.Serialization.encodeToTag
import btpos.mcmods.dungeondesignerlib.builder.nbt.TriggerBoundsTag
import btpos.mcmods.dungeondesignerlib.builder.blocks.actors.TileTriggerHolder.TriggerHolderState
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.phys.AABB
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals


class TriggerHolder {
	val bounds = AABB(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
	val id = UUID(123, 456)
	
	val state = TriggerHolderState(bounds, id)
	
	@Test
	fun serialize_toTag() {
		val expected = CompoundTag().also {
			it.put(TriggerHolderState.TAGKEY_BOUNDS, TriggerBoundsTag(bounds).tag)
			it.put(TriggerHolderState.TAGKEY_PLACER, UUIDUtil.CODEC.encodeToTag(id))
		}
		
		val encoded = CompoundTag().also {
			state.writeAsNbt(it)
		}
		
		assertEquals(expected, encoded)
	}
	
	@Test
	fun serialize_fromTag() {
		val tag = CompoundTag().also {
			it.put(TriggerHolderState.TAGKEY_BOUNDS, TriggerBoundsTag(bounds).tag)
			it.put(TriggerHolderState.TAGKEY_PLACER, UUIDUtil.CODEC.encodeToTag(id))
		}
		
		val decodedState = TriggerHolderState()
		
		decodedState.populateFromNbt(tag)
		
		assertEquals(state.trigger, decodedState.trigger)
		assertEquals(state.placer, decodedState.placer)
	}
}