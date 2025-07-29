package btpos.unittest.dungeondesignerlib.builder.blocks.actors

//class TriggerHolderStateTest {
//    val bounds = AABB(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
//    val id = UUID(123, 456)
//
//    @Test
//    fun serialize_toTag() {
//        val state = TriggerHolderState(bounds, id)
//
//        val expected = CompoundTag().also {
//            it.put(TriggerHolderState.TAGKEY_BOUNDS, Serialization.CODEC_AABB_BLOCK.encodeToTag(bounds))
//            it.put(TriggerHolderState.TAGKEY_PLACER, UUIDUtil.CODEC.encodeToTag(id))
//        }
//
//        val actual = CompoundTag().also {
//            state.writeAsNbt(it)
//        }
//
//        assertEquals(expected, actual)
//    }
//
//    @Test
//    fun serialize_fromTag() {
//        val expectedState = TriggerHolderState(bounds, id)
//
//        val tag = TriggerHolderState.CODEC.encodeToTag(expectedState) as CompoundTag
//
//        val decodedState = TriggerHolderState().apply {
//            populateFromNbt(tag)
//        }
//
//        assertEquals(expectedState.trigger, decodedState.trigger)
//        assertEquals(expectedState.placer, decodedState.placer)
//    }
//}
//
//// TODO unit test these during gametests
//class TriggerVarItemConverterTest {
//    @Test
//    fun `Able to read values from an incoming item's tag`() {
//        // Expected values
//        val testName = "bob marley"
//        val firstPos = BlockPos(1,2,3); val secondPos = BlockPos(4,5,6)
//
//        // Setup
//        val sourceTag = CompoundTag().also {
//            DisplayNameGetter(it).nameJson = testName
//            getOrCreateTriggerNbt(it).let(::TriggerBoundsTag).run {
//                first = firstPos
//                second = secondPos
//            }
//        }
//
//        val converter = TriggerVarItemConverter()
//        with (converter) {
//            sourceTag.readFromTag()
//        }
//
//
//        // Tests
//        with (converter) {
//            assertAll(
//                { assertEquals(testName, name) },
//                { assertEquals(AABB(firstPos, secondPos), value) },
//            )
//        }
//    }
//
//    @Test
//    fun `Able to write values to an outgoing item's tag`() {
//        // Constants
//        val testName = "bob marley"
//        val firstPos = BlockPos(1,2,3); val secondPos = BlockPos(4,5,6)
//        val expectedAABB = AABB(firstPos, secondPos)
//
//        val expected = CompoundTag().also {
//            DisplayNameGetter(it).nameJson = testName
//            getOrCreateTriggerNbt(it).let(::TriggerBoundsTag).putAABB(expectedAABB)
//        }
//        val converter = TriggerVarItemConverter(expectedAABB, testName)
//
//        // Action
//        val actual = CompoundTag().also {
//            with (converter) {
//                it.writeToTag()
//            }
//        }
//
//
//        // Tests
//        assertEquals(expected, actual)
//    }
//}