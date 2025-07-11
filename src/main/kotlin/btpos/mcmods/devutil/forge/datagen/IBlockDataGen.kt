package btpos.mcmods.devutil.forge.datagen

import btpos.mcmods.devutil.forge.datagen.BlockStateMacros.multipartDsl
import btpos.mcmods.devutil.forge.datagen.BlockStateMacros.variantDsl
import net.minecraft.core.Direction
import net.minecraft.world.level.block.AbstractFurnaceBlock.FACING
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS
import net.minecraftforge.client.model.generators.BlockModelProvider
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ItemModelProvider

interface IBlockDataGen : IObjectData {
	fun getStatesAndModels(provider: BlockStateProvider) {
		provider.models().getModels()
		provider.getStates()
		provider.itemModels().getItems()
		
//		//? TESTING
//		provider
//			.getVariantBuilder(Blocks.ACACIA_LEAVES)
//			.partialState()
//			.with(FACING, Direction.DOWN)
//				.modelForState()
//				.modelFile(null)
//				.rotationY(90)
//				.addModel()
//
//
//		provider.variantDsl(Blocks.ACACIA_LEAVES) {
//			AXIS {
//				Direction.Axis.Y {
//					model {
//						modelFile(null)
//						rotationY(90)
//					}
//					BlockStateProperties.AGE_1 {
//						1 {
//							model {
//
//							}
//							model {
//
//							}
//						}
//					}
//				}
//			}
//		}
//
//		provider.getMultipartBuilder(Blocks.ACACIA_LOG) // Get multipart builder
//			.part() // Create part
//			.modelFile(null) // Can show 'redstoneDot'
//			.nextModel()
//			.modelFile(null).weight(40)
//			.addModel() // 'redstoneDot' is displayed when...
//			.useOr() // At least one of these conditions are true
//			.nestedGroup() // true when all grouped conditions are true
//			.condition(FACING, Direction.UP) // true when WEST_REDSTONE is NONE
//			.condition(FACING, null) // true when EAST_REDSTONE is NONE
//			.condition(FACING, null) // true when SOUTH_REDSTONE is NONE
//			.condition(FACING, null) // true when NORTH_REDSTONE is NONE
//			.endNestedGroup() // End group
//			.nestedGroup() // true when all grouped conditions are true
//			.condition(FACING, null, null) // true when EAST_REDSTONE is SIDE or UP
//			.condition(FACING, null, null) // true when NORTH_REDSTONE is SIDE or UP
//			.endNestedGroup() // End group
//			.end() // End condition block
//			.end() // Finish part
//			.part() // Create part
//			.modelFile(redstoneSide0) // Can show 'redstoneSide0'
//			.addModel() // 'redstoneSide0' is displayed when...
//			.condition(NORTH_REDSTONE, SIDE, UP) // NORTH_REDSTONE is SIDE or UP
//			.end() // Finish part
//			.part() // Create part
//			.modelFile(redstoneSideAlt0) // Can show 'redstoneSideAlt0'
//			.addModel() // 'redstoneSideAlt0' is displayed when...
//			.condition(SOUTH_REDSTONE, SIDE, UP) // SOUTH_REDSTONE is SIDE or UP
//			.end() // Finish part
//			.part() // Create part
//			.modelFile(redstoneSideAlt1) // Can show 'redstoneSideAlt1'
//			.rotationY(270) // Rotates 'redstoneSideAlt1' 270 degrees on the Y axis
//			.addModel() // 'redstoneSideAlt1' is displayed when...
//			.condition(EAST_REDSTONE, SIDE, UP) // EAST_REDSTONE is SIDE or UP
//			.end() // Finish part
//			.part() // Create part
//			.modelFile(redstoneSide1) // Can show 'redstoneSide1'
//			.rotationY(270) // Rotates 'redstoneSide1' 270 degrees on the Y axis
//			.addModel() // 'redstoneSide1' is displayed when...
//			.condition(WEST_REDSTONE, SIDE, UP) // WEST_REDSTONE is SIDE or UP
//			.end() // Finish part
//			.part() // Create part
//			.modelFile(redstoneUp) // Can show 'redstoneUp'
//			.addModel() // 'redstoneUp' is displayed when...
//			.condition(NORTH_REDSTONE, UP) // NORTH_REDSTONE is UP
//			.end() // Finish part
//			.part() // Create part
//			.modelFile(redstoneUp) // Can show 'redstoneUp'
//			.rotationY(90) // Rotates 'redstoneUp' 90 degrees on the Y axis
//			.addModel() // 'redstoneUp' is displayed when...
//			.condition(EAST_REDSTONE, UP) // EAST_REDSTONE is UP
//			.end() // Finish part
//			.part() // Create part
//			.modelFile(redstoneUp) // Can show 'redstoneUp'
//			.rotationY(180) // Rotates 'redstoneUp' 180 degrees on the Y axis
//			.addModel() // 'redstoneUp' is displayed when...
//			.condition(SOUTH_REDSTONE, UP) // SOUTH_REDSTONE is UP
//			.end() // Finish part
//			.part() // Create part
//			.modelFile(redstoneUp) // Can show 'redstoneUp'
//			.rotationY(270) // Rotates 'redstoneUp' 270 degrees on the Y axis
//			.addModel() // 'redstoneUp' is displayed when...
//			.condition(WEST_REDSTONE, UP) // WEST_REDSTONE is UP
//			.end();
		
//		provider.multipartDsl(Blocks.ACACIA_LOG) {
//			part {
//				model {
//
//				}
//				model {
//
//				}
//				condition = or {
//
//				}
//			}
//		}
	}
	
	fun BlockModelProvider.getModels()
	
	fun BlockStateProvider.getStates()
	
	fun ItemModelProvider.getItems()
}