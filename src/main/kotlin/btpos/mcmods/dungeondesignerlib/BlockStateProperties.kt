package btpos.mcmods.dungeondesignerlib

import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty

val POWERED: BooleanProperty = BlockStateProperties.POWERED

/**
 * Dungeon logic blocks are made "immutable" when the designer "build" action has been invoked.
 * This makes them completely immune to changes from the outside.
 *
 * EDIT: Actually, we should have a completely separate block so we don't have to deal with some states having tile entities and some not
 */
val IMMUTABLE: BooleanProperty = BooleanProperty.create("immutable")

