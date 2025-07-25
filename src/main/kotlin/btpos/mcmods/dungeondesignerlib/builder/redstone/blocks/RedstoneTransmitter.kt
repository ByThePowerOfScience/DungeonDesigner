@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")

package btpos.mcmods.dungeondesignerlib.builder.redstone.blocks

import btpos.mcmods.devutil.common.SetBlockStateFlags
import btpos.mcmods.devutil.common.ext.kotlin.isNullOrTrue
import btpos.mcmods.devutil.common.ext.vanilla.asComponent

import btpos.mcmods.devutil.common.ext.vanilla.data.getCompoundOrNull
import btpos.mcmods.devutil.common.ext.vanilla.isClientSide
import btpos.mcmods.devutil.common.ext.vanilla.targetBlockEntity
import btpos.mcmods.devutil.common.ext.vanilla.world.get
import btpos.mcmods.devutil.common.ext.vanilla.world.with
import btpos.mcmods.devutil.common.util.BlockWithEntity
import btpos.mcmods.devutil.forge.datagen.IBlockDataGen
import btpos.mcmods.devutil.forge.datagen.blockLoc
import btpos.mcmods.devutil.forge.datagen.variantDsl
import btpos.mcmods.devutil.forge.ext.capability.getOrNull
import btpos.mcmods.devutil.forge.ext.capability.iterFullSlots
import btpos.mcmods.dungeondesignerlib.MOD_LOGGER
import btpos.mcmods.dungeondesignerlib.POWERED
import btpos.mcmods.dungeondesignerlib.builder.redstone.IWirelessRedstone
import btpos.mcmods.dungeondesignerlib.builder.redstone.IWirelessRedstone.Companion.NO_CHANNEL
import btpos.mcmods.dungeondesignerlib.builder.redstone.IWirelessRedstone.NbtAdapter
import btpos.mcmods.dungeondesignerlib.builder.redstone.IWirelessRedstoneTransmitter
import btpos.mcmods.dungeondesignerlib.builder.redstone.blocks.WirelessRedstoneItem.Companion.getData
import btpos.mcmods.dungeondesignerlib.builder.redstone.blocks.WirelessRedstoneItem.Companion.getOrCreateData
import btpos.mcmods.dungeondesignerlib.builder.redstone.items.ItemRemoteLinker
import btpos.mcmods.dungeondesignerlib.builder.world.dungeonBuilderData
import btpos.mcmods.dungeondesignerlib.registry.ModBlocks
import btpos.mcmods.dungeondesignerlib.registry.ModItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
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
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.items.IItemHandler

/**
 * In the builder phase, this stores the channel of an emitter that's linked to it, or otherwise can be linked to an emitter.  Think "RFTools Redstone Transmitter".
 * When compiled, this is literally just a block that can be powered, and the wireless emitter powers it directly.
 */
class BlockRedstoneTransmitter(props: Properties) : Block(props), BlockWithEntity<TileRedstoneTransmitter>, WirelessRedstoneBlock {
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
    
    override fun setPlacedBy(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pPlacer: LivingEntity?,
        pStack: ItemStack
    ) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack)
        
        if (pStack.item !== ModBlocks.REDSTONE_TRANSMITTER_ITEM)
            return
        
        if (!pLevel.isClientSide) {
            pLevel.getOurEntity(pPos)?.let {
                val redstoneHandler = (pLevel as ServerLevel).dataStorage.dungeonBuilderData.state.redstoneHandler
                it.channel = pStack.getData()?.channel ?: redstoneHandler.makeNewChannel()
            } ?: return MOD_LOGGER.error("No block entity found at pos $pPos!", Throwable())
            
        }
    }
    
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
                pLevel.setBlockAndUpdate(pPos, pState.with(POWERED, !isPowered))
                if (shouldBePowered) {
                    pLevel.getOurEntity(pPos)?.onStartReceivingSignal(pLevel)
                    doRemoteLinkers(pLevel, pPos, true)
                } else {
                    pLevel.getOurEntity(pPos)?.onStopReceivingSignal(pLevel)
                    doRemoteLinkers(pLevel, pPos, false)
                }
            }
        }
    }
    
    private fun doRemoteLinkers(pLevel: ServerLevel, pPos: BlockPos, powered: Boolean) {
        getRemoteLinkers(pLevel, pPos).forEach {
            val targetPos = ItemRemoteLinker.getData(it)?.target ?: return@forEach
            val state = pLevel.getBlockState(targetPos)
            if (state.hasProperty(POWERED))
                pLevel.setBlockAndUpdate(targetPos, state.with(POWERED, powered))
        }
    }
    
    fun getRemoteLinkers(level: ServerLevel, pos: BlockPos): Sequence<ItemStack> {
        val cap = level.getBlockEntity(pos.above())?.getCapability(ForgeCapabilities.ITEM_HANDLER, null)?.getOrNull() ?: return emptySequence()
        return cap.iterFullSlots().filter { it.item == ModItems.REMOTE_LINKER }
    }
    
    override fun use(pState: BlockState, pLevel: Level, pPos: BlockPos, pPlayer: Player, pHand: InteractionHand, pHit: BlockHitResult): InteractionResult {
        super<WirelessRedstoneBlock>.onRightClick(pLevel, pPos, pPlayer)
        
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit)
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
        pStack.getOrCreateData().channel = this.channel
    }
}

class ItemBlockRedstoneTransmitter(props: Properties) : BlockItem(ModBlocks.REDSTONE_TRANSMITTER, props), WirelessRedstoneItem {
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
    
    override fun appendHoverText(pStack: ItemStack, pLevel: Level?, pTooltip: MutableList<Component>, pFlag: TooltipFlag) {
        super<BlockItem>.appendHoverText(pStack, pLevel, pTooltip, pFlag)
        super<WirelessRedstoneItem>.appendHoverText(pStack, pLevel, pTooltip, pFlag)
    }
}

/**
 * Consolidates some logic for items wrapping wireless redstone so my IDE stops yelling at me about duplicates,
 *  namely hover text and assigning channels to the block entity you click on.
 */
interface WirelessRedstoneItem {
    companion object {
        fun ItemStack.getOrCreateData(): NbtAdapter = NbtAdapter(this.getOrCreateTagElement("dungeondesigner"))
        fun ItemStack.getData(): NbtAdapter? = this.tag?.getCompoundOrNull("dungeondesigner")?.let(::NbtAdapter)
    }
    
    fun appendHoverText(pStack: ItemStack, pLevel: Level?, pTooltip: MutableList<Component>, pFlag: TooltipFlag) {
        pStack.getData()?.channel?.takeIf { it != NO_CHANNEL }?.let {
            pTooltip += "Channel: $it".asComponent()
        }
    }
    
    fun useOn(target: BlockEntity?, pContext: UseOnContext): Boolean {
        if (target !is IWirelessRedstone) { // TODO make this a capability instead
            return false
        }
        
        if (!pContext.isClientSide) {
            pContext.itemInHand.getOrCreateData().channel = target.channel
        }
        
        return true
    }
}

interface WirelessRedstoneBlock {
    fun onRightClick(pLevel: Level, pPos: BlockPos, pPlayer: Player) {
        if (pLevel.isClientSide)
            return
        val ent = pLevel.getBlockEntity(pPos) as? IWirelessRedstone ?: return
        pPlayer.sendSystemMessage("Channel: ${ent.channel}".asComponent())
    }
}