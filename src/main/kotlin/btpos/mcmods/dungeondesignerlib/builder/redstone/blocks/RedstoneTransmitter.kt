@file:Suppress("OVERRIDE_DEPRECATION")

package btpos.mcmods.dungeondesignerlib.builder.redstone.blocks

import btpos.mcmods.devutil.common.SetBlockStateFlags
import btpos.mcmods.devutil.common.ext.kotlin.isNullOrTrue

import btpos.mcmods.devutil.common.ext.vanilla.data.getCompoundOrNull
import btpos.mcmods.devutil.common.ext.vanilla.isClientSide
import btpos.mcmods.devutil.common.ext.vanilla.targetBlockEntity
import btpos.mcmods.devutil.common.ext.vanilla.world.get
import btpos.mcmods.devutil.common.ext.vanilla.world.with
import btpos.mcmods.devutil.common.util.BlockWithEntity
import btpos.mcmods.devutil.forge.datagen.IBlockDataGen
import btpos.mcmods.devutil.forge.datagen.blockLoc
import btpos.mcmods.devutil.forge.datagen.variantDsl
import btpos.mcmods.dungeondesignerlib.POWERED
import btpos.mcmods.dungeondesignerlib.builder.redstone.IWirelessRedstone
import btpos.mcmods.dungeondesignerlib.builder.redstone.IWirelessRedstone.Companion.NO_CHANNEL
import btpos.mcmods.dungeondesignerlib.builder.redstone.IWirelessRedstone.NbtAdapter
import btpos.mcmods.dungeondesignerlib.builder.redstone.IWirelessRedstoneTransmitter
import btpos.mcmods.dungeondesignerlib.builder.redstone.blocks.ItemBlockRedstoneTransmitter.Companion.getOrCreateData
import btpos.mcmods.dungeondesignerlib.builder.world.dungeonBuilderData
import btpos.mcmods.dungeondesignerlib.registry.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * In the builder phase, this stores the channel of an emitter that's linked to it, or otherwise can be linked to an emitter.  Think "RFTools Redstone Transmitter".
 * When compiled, this is literally just a block that can be powered, and the wireless emitter powers it directly.
 */
class BlockRedstoneTransmitter(props: Properties) : Block(props), BlockWithEntity<TileRedstoneTransmitter> {
    companion object : IBlockDataGen {
        override val id: String
            get() = "redstone_transmitter"
        
        @Suppress("DuplicatedCode")
        override fun BlockStateProvider.buildModelsAndStates() {
            val txBase = "redstone/transmitter/transmitter"
            
            val off = models().cubeColumn(id.powered(false), "${txBase}_sides".powered(false).blockLoc(), "${txBase}_top".powered(false).blockLoc())
            val on = models().cubeColumn(id.powered(true), "${txBase}_sides".powered(true).blockLoc(), "${txBase}_top".powered(true).blockLoc())
            
            variantDsl(ModBlocks.REDSTONE_TRANSMITTER) {
                POWERED {
                    true {
                        model {
                            modelFile(on)
                        }
                    }
                    false {
                        model {
                            modelFile(off)
                        }
                    }
                }
            }
            
            simpleBlockItem(ModBlocks.REDSTONE_TRANSMITTER, off)
        }
    }
    
    //region Setup
    init {
        registerDefaultState(stateDefinition.any().with(POWERED, false))
    }
    
    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(pBuilder)
        pBuilder.add(POWERED)
    }
    
    override fun getEntityType() = ModBlocks.REDSTONE_TRANSMITTER_ENTITY
    //endregion
    
    override fun neighborChanged(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pNeighborBlock: Block,
        pNeighborPos: BlockPos,
        pMovedByPiston: Boolean
    ) {
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston)
        if (pLevel is ServerLevel) {
            val isPowered = pState[POWERED]
            val shouldBePowered = pLevel.hasNeighborSignal(pPos)
            if (isPowered != shouldBePowered) {
                pLevel.setBlockAndUpdate(pPos, pState.with(POWERED, !shouldBePowered))
                if (isPowered) {
                    pLevel.getOurEntity(pPos)?.onStartReceivingSignal(pLevel)
                } else {
                    pLevel.getOurEntity(pPos)?.onStopReceivingSignal(pLevel)
                }
            }
        }
    }
    
    
    //region Redstone
    override fun canConnectRedstone(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction?) = true
    //endregion
    
    override fun asItem(): Item {
        return ModBlocks.REDSTONE_TRANSMITTER_ITEM
    }
}

class TileRedstoneTransmitter(pPos: BlockPos, pState: BlockState)
    : BlockEntity(ModBlocks.REDSTONE_TRANSMITTER_ENTITY, pPos, pState), IWirelessRedstoneTransmitter, IWirelessRedstone by IWirelessRedstone.make(NO_CHANNEL)
{
    override fun saveToItem(pStack: ItemStack) {
        super.saveToItem(pStack)
        pStack.getOrCreateData().channel = this.channel
    }
}

class ItemBlockRedstoneTransmitter(props: Properties) : BlockItem(ModBlocks.REDSTONE_TRANSMITTER, props) {
    companion object {
        fun ItemStack.getOrCreateData(): NbtAdapter = NbtAdapter(this.getOrCreateTagElement("dungeondesigner"))
        fun ItemStack.getData(): NbtAdapter? = this.tag?.getCompoundOrNull("dungeondesigner")?.let(::NbtAdapter)
    }
    
    override fun useOn(pContext: UseOnContext): InteractionResult {
        val target = pContext.targetBlockEntity
        if (target !is IWirelessRedstone) { // TODO make this a capability instead
            return InteractionResult.PASS
        }
        
        if (!pContext.isClientSide) {
            pContext.itemInHand.getOrCreateData().channel = target.channel
        }
        
        return InteractionResult.sidedSuccess(pContext.isClientSide)
    }
    
    override fun placeBlock(pContext: BlockPlaceContext, pState: BlockState): Boolean {
        return super.placeBlock(pContext, pState)
    }
}