package btpos.mcmods.dungeondesignerlib.blocks

import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty

val POWERED: BooleanProperty = BlockStateProperties.POWERED

/**
 * Dungeon logic blocks are made "immutable" when the designer "build" action has been invoked.
 * This makes them completely immune to changes from the outside.
 */
val IMMUTABLE: BooleanProperty = BooleanProperty.create("immutable")