package btpos.mcmods.devutil.forge.datagen

import btpos.mcmods.devutil.forge.datagen.BlockStateMacros.BaseVariantBuilder
import btpos.mcmods.devutil.forge.datagen.BlockStateMacros.MultipartBuilder
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.random.Weighted
import net.minecraft.util.random.WeightedList
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.Property
import kotlin.collections.iterator


@DslMarker
annotation class BlockStateDataGen
@DslMarker
annotation class MultiPartDataGenDsl



//? TESTING
/*		provider
			.getVariantBuilder(Blocks.ACACIA_LEAVES)
			.partialState()
			.with(FACING, Direction.DOWN)
				.modelForState()
				.modelFile(null)
				.rotationY(90)
				.addModel()


		provider.variantDsl(Blocks.ACACIA_LEAVES) {
			AXIS {
				Direction.Axis.Y {
					model {
						modelFile(null)
						rotationY(90)
					}
					BlockStateProperties.AGE_1 {
						1 {
							model {

							}
							model {

							}
						}
					}
				}
			}
		}

		provider.getMultipartBuilder(Blocks.ACACIA_LOG) // Get multipart builder
			.part() // Create part
			.modelFile(null) // Can show 'redstoneDot'
			.nextModel()
			.modelFile(null).weight(40)
			.addModel() // 'redstoneDot' is displayed when...
			.useOr() // At least one of these conditions are true
			.nestedGroup() // true when all grouped conditions are true
			.condition(FACING, Direction.UP) // true when WEST_REDSTONE is NONE
			.condition(FACING, null) // true when EAST_REDSTONE is NONE
			.condition(FACING, null) // true when SOUTH_REDSTONE is NONE
			.condition(FACING, null) // true when NORTH_REDSTONE is NONE
			.endNestedGroup() // End group
			.nestedGroup() // true when all grouped conditions are true
			.condition(FACING, null, null) // true when EAST_REDSTONE is SIDE or UP
			.condition(FACING, null, null) // true when NORTH_REDSTONE is SIDE or UP
			.endNestedGroup() // End group
			.end() // End condition block
			.end() // Finish part
			.part() // Create part
			.modelFile(redstoneSide0) // Can show 'redstoneSide0'
			.addModel() // 'redstoneSide0' is displayed when...
			.condition(NORTH_REDSTONE, SIDE, UP) // NORTH_REDSTONE is SIDE or UP
			.end() // Finish part
			.part() // Create part
			.modelFile(redstoneSideAlt0) // Can show 'redstoneSideAlt0'
			.addModel() // 'redstoneSideAlt0' is displayed when...
			.condition(SOUTH_REDSTONE, SIDE, UP) // SOUTH_REDSTONE is SIDE or UP
			.end() // Finish part
			.part() // Create part
			.modelFile(redstoneSideAlt1) // Can show 'redstoneSideAlt1'
			.rotationY(270) // Rotates 'redstoneSideAlt1' 270 degrees on the Y axis
			.addModel() // 'redstoneSideAlt1' is displayed when...
			.condition(EAST_REDSTONE, SIDE, UP) // EAST_REDSTONE is SIDE or UP
			.end() // Finish part
			.part() // Create part
			.modelFile(redstoneSide1) // Can show 'redstoneSide1'
			.rotationY(270) // Rotates 'redstoneSide1' 270 degrees on the Y axis
			.addModel() // 'redstoneSide1' is displayed when...
			.condition(WEST_REDSTONE, SIDE, UP) // WEST_REDSTONE is SIDE or UP
			.end() // Finish part
			.part() // Create part
			.modelFile(redstoneUp) // Can show 'redstoneUp'
			.addModel() // 'redstoneUp' is displayed when...
			.condition(NORTH_REDSTONE, UP) // NORTH_REDSTONE is UP
			.end() // Finish part
			.part() // Create part
			.modelFile(redstoneUp) // Can show 'redstoneUp'
			.rotationY(90) // Rotates 'redstoneUp' 90 degrees on the Y axis
			.addModel() // 'redstoneUp' is displayed when...
			.condition(EAST_REDSTONE, UP) // EAST_REDSTONE is UP
			.end() // Finish part
			.part() // Create part
			.modelFile(redstoneUp) // Can show 'redstoneUp'
			.rotationY(180) // Rotates 'redstoneUp' 180 degrees on the Y axis
			.addModel() // 'redstoneUp' is displayed when...
			.condition(SOUTH_REDSTONE, UP) // SOUTH_REDSTONE is UP
			.end() // Finish part
			.part() // Create part
			.modelFile(redstoneUp) // Can show 'redstoneUp'
			.rotationY(270) // Rotates 'redstoneUp' 270 degrees on the Y axis
			.addModel() // 'redstoneUp' is displayed when...
			.condition(WEST_REDSTONE, UP) // WEST_REDSTONE is UP
			.end();

		provider.multipartDsl(Blocks.ACACIA_LOG) {
			part {
				model {

				}
				model {

				}
				condition = or {

				}
			}
		}*/

private fun rl(namespace: String, path: String) = ResourceLocation.fromNamespaceAndPath(namespace, path)

class BlockStateBuilder(val mod_id: String) {
	fun modLoc(path: String): ResourceLocation {
		return rl(mod_id, path)
	}
}

class ModelData {
	lateinit var modelFile: String
	var weight: Int = 1
	var yRot: Int? = null
	var xRot: Int? = null
	var uv_lock: Boolean? = null
}

data class PartialBlockstate(private val propertyValues: MutableMap<Property<*>, Comparable<*>> = mutableMapOf(), val models: MutableList<ModelData> = mutableListOf()) {
	fun <T : Comparable<T>> withProperty(prop: Property<T>, value: T): PartialBlockstate {
		propertyValues[prop] = value
		return this
	}
	
	fun addModel(data: ModelData) {
		this.models += data
	}
	
	fun build(): Pair<Map<Property<*>, Comparable<*>>, WeightedList<ModelData>> {
		
		return propertyValues to WeightedList.of(models.map { Weighted(it, it.weight) })
	}
}

object BlockStateMacros {
	/**
	 * Only allows for selecting a block's property, like AXIS
	 */
	@BlockStateDataGen
	class BaseVariantBuilder(
		/**
		 * Get the partial state for this branch, made fresh from the base builder with all previous properties applied.
		 *
		 * Identical to tacking on every property yourself.
		 */
		val getPartialState: () -> PartialBlockstate
	) {
		/**
		 * AXIS {
		 *  ...
		 * }
		 */
		operator fun <U : Comparable<U>> Property<U>.invoke(action: VariantBuilderPropertySwitch<U>.() -> Unit) {
			VariantBuilderPropertySwitch(this, getPartialState).action()
		}
		
		/**
		 * This function calls [ConfiguredModel.Builder.addModel]. Don't call it unless you want to register this twice.
		 * Unlike the standard builder, this keeps you at the nested blockstate after being invoked.
		 */
		fun model(action: ModelData.() -> Unit) {
			
			getPartialState().addModel()
			
			currentModelBuilder = modelBuilder // save it for the next model block
		}
		
		fun build() {
			currentModelBuilder?.addModel() // delay calling addModel so we don't call it early
		}
	}
	
	/**
	 * Only allows for selecting a property value, like Axis.Y
	 */
	@BlockStateDataGen
	class VariantBuilderPropertySwitch<T : Comparable<T>>(
		private val prop: Property<T>,
		private val stateRestorer: () -> PartialBlockstate
	) {
		/**
		 * ```
		 * Axis.Y {
		 *
		 * }
		 * ```
		 */
		operator fun T.invoke(action: BaseVariantBuilder.() -> Unit) {
			BaseVariantBuilder({ stateRestorer().withProperty(prop, this) }).run {
				action()
				build()
			}
		}
	}
	
	
	
	@MultiPartDataGenDsl
	class MultipartBuilder {
		private val parts = mutableListOf<MultipartPartBuilder>()
		
		fun part(action: MultipartPartBuilder.() -> Unit) {
			parts += MultipartPartBuilder().apply(action)
		}
		
		internal fun build(forgeBuilder: MultiPartBlockStateBuilder) {
			var partBuilder = forgeBuilder
			for (part in parts) {
				partBuilder = partBuilder.apply(part::build)
			}
		}
	}
	
	@MultiPartDataGenDsl
	class MultipartPartBuilder {
		private val modelConfigs = mutableListOf<ConfiguredModel.Builder<*>.() -> Unit>()
		
		fun model(modelConfig: ConfiguredModel.Builder<*>.() -> Unit) {
			this.modelConfigs += modelConfig
		}
		
		lateinit var condition: ConditionBlock
		
		fun or(action: ConditionBlock.() -> Unit): ConditionBlock {
			return ConditionBlock(true).apply(action)
		}
		
		fun and(action: ConditionBlock.() -> Unit): ConditionBlock {
			return ConditionBlock(false).apply(action)
		}
		
		internal fun build(builder: MultiPartBlockStateBuilder): MultiPartBlockStateBuilder { // end() SHOULD return `this`, but just to be safe:
			require(modelConfigs.isNotEmpty()) { "Cannot have a part with no model!" }
			require(::condition.isInitialized) { "Multipart model must have a condition." }
			
			var modelBuilder = builder.part() // init part
			
			modelBuilder.let(modelConfigs.first()) // do first model
			
			if (modelConfigs.size > 1) { // call nextmodel instead of addmodel
				for (i in 1..<modelConfigs.size) {
					modelBuilder = modelBuilder.nextModel()
					modelBuilder.apply(modelConfigs[i])
				}
			}
			
			var conditionBuilder = modelBuilder.addModel()
			
			if (condition.isOr)
				conditionBuilder = conditionBuilder.useOr()
			
			for (cond in condition.listOfConditions) {
				@Suppress("UNCHECKED_CAST") // If I don't explicitly cast this, the _Kotlin compiler itself_ will freeze.
				conditionBuilder = when (cond) {
					is ConditionType.NestedCondBlock -> {
						val block = cond.block
						val nested = conditionBuilder.nestedGroup()
						block.applyToCondGroup(nested)
						nested.endNestedGroup().end()
					}
					is ConditionType.CondPair<*> -> conditionBuilder.condition(cond.property as Property<Comparable<Any>>, *(cond.values as Array<Comparable<Any>>))
				}
			}
			
			return conditionBuilder.end()
		}
	}
	
	internal sealed class ConditionType {
		class CondPair<T : Comparable<T>>(val property: Property<T>, vararg val values: T) : ConditionType()
		
		class NestedCondBlock(val block: ConditionBlock) : ConditionType()
	}
	
	@MultiPartDataGenDsl
	class ConditionBlock(internal var isOr: Boolean = false) {
		internal val listOfConditions = mutableListOf<ConditionType>()
		
		operator fun <T : Comparable<T>> Property<T>.invoke(vararg condition: T) {
			listOfConditions += ConditionType.CondPair(this, *condition)
		}
		
		/**
		 * Allows `WEST_REDSTONE in arrayOf(NONE, LEFT, RIGHT, UP)`
		 * but without needing inline reified so we can keep listOfConditions private
		 */
		operator fun <T : Comparable<T>> Array<T>.contains(property: Property<T>): Boolean {
			listOfConditions += ConditionType.CondPair(property, *this)
			return true
		}
		
		fun or(action: ConditionBlock.() -> Unit) {
			listOfConditions += ConditionType.NestedCondBlock(ConditionBlock(true).apply(action))
		}
		
		fun and(action: ConditionBlock.() -> Unit) {
			listOfConditions += ConditionType.NestedCondBlock(ConditionBlock(false).apply(action))
		}
		
		
		internal fun applyToCondGroup(group: ForgeCondGroup) {
			var group = group
			if (isOr) {
				group = group.useOr()
			}
			
			for (cond in listOfConditions) {
				@Suppress("UNCHECKED_CAST")
				group = when (cond) {
					is ConditionType.CondPair<*> -> group.condition(cond.property as Property<Comparable<Any>>, *(cond.values as Array<Comparable<Any>>))
					is ConditionType.NestedCondBlock -> {
						val nested = group.nestedGroup()
						cond.block.applyToCondGroup(nested)
						nested.endNestedGroup()
					}
				}
			}
		}
	}
}

fun BlockStateProvider.variantDsl(block: Block, action: BaseVariantBuilder.() -> Unit) {
	val builder = this.getVariantBuilder(block)
	BaseVariantBuilder({ builder.partialState() }).action()
}

fun BlockStateProvider.multipartDsl(block: Block, configuration: MultipartBuilder.() -> Unit) {
	val dslBuilder = MultipartBuilder().apply(configuration)
	this.getMultipartBuilder(block).apply(dslBuilder::build)
}

private typealias ForgeCondGroup = MultiPartBlockStateBuilder.PartBuilder.ConditionGroup

fun BlockStateMacros.VariantBuilderPropertySwitch<Direction>.rotateForEachHorizontal(model: BlockModelBuilder, additionalAction:  ConfiguredModel.Builder<*>.(Int, Direction) -> Unit = { idx, dir ->}) {
	for ((i, dir) in listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST).iterator().withIndex()) {
		dir {
			model {
				modelFile(model)
				if (i != 0)
					rotationY(i * 90)
				additionalAction(i, dir)
			}
		}
	}
}