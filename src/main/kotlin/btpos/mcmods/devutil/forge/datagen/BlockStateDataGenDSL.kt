package btpos.mcmods.devutil.forge.datagen

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.Property
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ConfiguredModel
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder


@DslMarker
annotation class BlockStateDataGen
@DslMarker
annotation class MultiPartDataGenDsl


object BlockStateMacros {
	/**
	 * Only allows for selecting a block's property, like AXIS
	 */
	@BlockStateDataGen
	class BaseVariantBuilder(
		/**
		 * builds the partial state from scratch before each model, which is the same as the real builder does
		 * adds on the properties we traversed to get here before
		 */
		private val stateRestorer: () -> VariantBlockStateBuilder.PartialBlockstate
	) {
		var currentModelBuilder: ConfiguredModel.Builder<*>? = null
		
		/**
		 * AXIS {
		 *  ...
		 * }
		 */
		operator fun <U : Comparable<U>> Property<U>.invoke(action: VariantBuilderPropertySwitch<U>.() -> Unit) {
			VariantBuilderPropertySwitch(this, stateRestorer).action()
		}
		
		/**
		 * This function calls [ConfiguredModel.Builder.addModel]. Don't call it unless you want to register this twice.
		 * Unlike the standard builder, this keeps you at the nested blockstate.
		 *
		 * model {
		 *
		 * }
		 *
		 * DON'T CALL `nextModel`!!! The next `model {}` block will do it for you.
		 */
		fun model(action: ConfiguredModel.Builder<*>.() -> Unit) {
			currentModelBuilder?.nextModel() // if we've made a model before, call nextModel
			
			val modelBuilder = currentModelBuilder ?: stateRestorer().modelForState()
			modelBuilder.action()
			
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
		private val stateRestorer: () -> VariantBlockStateBuilder.PartialBlockstate
	) {
		/**
		 * ```
		 * Axis.Y {
		 *
		 * }
		 * ```
		 */
		operator fun T.invoke(action: BaseVariantBuilder.() -> Unit) {
			BaseVariantBuilder({ stateRestorer().with(prop, this) }).run {
				action()
				build()
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

private typealias ForgeCondGroup = MultiPartBlockStateBuilder.PartBuilder.ConditionGroup