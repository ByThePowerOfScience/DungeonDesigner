@file:Suppress("OVERRIDE_DEPRECATION")

package btpos.mcmods.dungeondesignerlib.builder.redstone.blocks

import btpos.mcmods.devutil.common.ext.vanilla.data.getCompoundOrNull
import btpos.mcmods.devutil.common.ext.vanilla.data.getOrCreateCompound
import btpos.mcmods.devutil.common.ext.vanilla.isClientSide
import btpos.mcmods.devutil.common.ext.vanilla.targetBlockEntity
import btpos.mcmods.devutil.common.ext.vanilla.world.get
import btpos.mcmods.devutil.common.ext.vanilla.world.with
import btpos.mcmods.devutil.common.util.BlockWithEntity
import btpos.mcmods.devutil.forge.datagen.IBlockDataGen
import btpos.mcmods.devutil.forge.datagen.blockLoc
import btpos.mcmods.devutil.forge.datagen.variantDsl
import btpos.mcmods.dungeondesignerlib.MOD_LOGGER
import btpos.mcmods.dungeondesignerlib.POWERED
import btpos.mcmods.dungeondesignerlib.builder.redstone.IWirelessRedstone
import btpos.mcmods.dungeondesignerlib.builder.redstone.IWirelessRedstone.Companion.NO_CHANNEL
import btpos.mcmods.dungeondesignerlib.builder.redstone.IWirelessRedstone.NbtAdapter
import btpos.mcmods.dungeondesignerlib.builder.redstone.blocks.WirelessRedstoneItem.Companion.getData
import btpos.mcmods.dungeondesignerlib.builder.redstone.blocks.WirelessRedstoneItem.Companion.getOrCreateData
import btpos.mcmods.dungeondesignerlib.builder.world.dungeonBuilderData
import btpos.mcmods.dungeondesignerlib.registry.ModBlocks
import btpos.mcmods.dungeondesignerlib.registry.ModItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NumericTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.client.model.generators.BlockStateProvider

/**
 * In the builder phase, this stores the channel of an emitter that's linked to it, or otherwise can be linked to an emitter.  Think "RFTools Redstone Receiver".
 * When compiled, this is literally just a block that can be powered, and the wireless emitter powers it directly.
 */
class BlockRedstoneReceiver(props: Properties) : Block(props), BlockWithEntity<TileRedstoneReceiver>, WirelessRedstoneBlock {
    companion object : IBlockDataGen {
        override val id: String
            get() = "redstone_receiver"
        
        override fun BlockStateProvider.buildModelsAndStates() {
            val txBase = "redstone/receiver/receiver"
            
            val off = models().cubeColumn(id.powered(false), "${txBase}_sides".powered(false).blockLoc(), "${txBase}_top".powered(false).blockLoc())
            val on = models().cubeColumn(id.powered(true), "${txBase}_sides".powered(true).blockLoc(), "${txBase}_top".powered(true).blockLoc())
            
            variantDsl(ModBlocks.REDSTONE_RECEIVER) {
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
            
            simpleBlockItem(ModBlocks.REDSTONE_RECEIVER, off)
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
    
    override fun getEntityType() = ModBlocks.REDSTONE_RECEIVER_ENTITY
    //endregion
    
    //region Placement
    override fun setPlacedBy(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pPlacer: LivingEntity?,
        pStack: ItemStack
    ) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack)
        
        if (pStack.item !== ModBlocks.REDSTONE_RECEIVER_ITEM)
            return
        
        if (!pLevel.isClientSide) {
            pLevel.getOurEntity(pPos)?.let {
                val redstoneHandler = (pLevel as ServerLevel).dataStorage.dungeonBuilderData.state.redstoneHandler
                it.channel = redstoneHandler.registerWirelessReceiver(pStack.getData()?.channel ?: NO_CHANNEL, pPos)
                if (redstoneHandler.isChannelPowered(it.channel)) {
                    pLevel.setBlockAndUpdate(pPos, pState.with(POWERED, true))
                }
            } ?: return MOD_LOGGER.error("No block entity found at pos $pPos!", Throwable())
            
        }
    }
    
    override fun use(pState: BlockState, pLevel: Level, pPos: BlockPos, pPlayer: Player, pHand: InteractionHand, pHit: BlockHitResult): InteractionResult {
        super<WirelessRedstoneBlock>.onRightClick(pLevel, pPos, pPlayer)
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit)
    }
    
    
    override fun onRemove(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pNewState: BlockState,
        pMovedByPiston: Boolean
    ) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston)
        
        if (!pLevel.isClientSide && pNewState.block != pState.block) {
            pLevel.getOurEntity(pPos)?.let {
                (pLevel as ServerLevel).dataStorage.dungeonBuilderData.state.redstoneHandler.unregisterWirelessReceiver(it.channel, pPos)
            }
        }
    }
    //endregion
    
    //region Redstone
    override fun canConnectRedstone(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction?) = true
    
    override fun getSignal(pState: BlockState, pLevel: BlockGetter, pPos: BlockPos, pDirection: Direction): Int {
        return if (pState[POWERED]) 15 else 0
    }
    //endregion
    
    override fun asItem(): Item {
        return ModBlocks.REDSTONE_RECEIVER_ITEM
    }
}

class TileRedstoneReceiver(pPos: BlockPos, pState: BlockState)
    : BlockEntity(ModBlocks.REDSTONE_RECEIVER_ENTITY, pPos, pState), IWirelessRedstone by IWirelessRedstone.make(NO_CHANNEL)
{
    override fun saveAdditional(pTag: CompoundTag) {
        super.saveAdditional(pTag)
        pTag.putInt("channel", channel)
    }
    
    override fun load(pTag: CompoundTag) {
        super.load(pTag)
        channel = pTag.getInt("channel")
    }
    override fun saveToItem(pStack: ItemStack) {
        super.saveToItem(pStack)
    }
}

/**
 * Allows us to right-click on a transmitter or receiver in the world and copy its channel
 */
class ItemBlockRedstoneReceiver(props: Properties) : BlockItem(ModBlocks.REDSTONE_RECEIVER, props), WirelessRedstoneItem {
    
    @Suppress("DuplicatedCode")
    override fun useOn(pContext: UseOnContext): InteractionResult {
        val target = pContext.targetBlockEntity
        
        if (super<WirelessRedstoneItem>.useOn(target, pContext)) {
            return InteractionResult.sidedSuccess(pContext.isClientSide)
        }
        
        return super<BlockItem>.useOn(pContext)
    }
    
    override fun placeBlock(pContext: BlockPlaceContext, pState: BlockState): Boolean {
        return super.placeBlock(pContext, pState)
    }
    
    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltip: MutableList<Component>,
        pFlag: TooltipFlag
    ) {
        super<BlockItem>.appendHoverText(pStack, pLevel, pTooltip, pFlag)
        super<WirelessRedstoneItem>.appendHoverText(pStack, pLevel, pTooltip, pFlag)
    }
}