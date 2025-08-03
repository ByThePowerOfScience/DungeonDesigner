package btpos.mcmods.dungeondesignerlib.builder.redstone.items

import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.ext.vanilla.data.getBlockPos
import btpos.mcmods.devutil.common.ext.vanilla.data.getCompoundOrNull
import btpos.mcmods.devutil.common.ext.vanilla.data.setOrRemove
import btpos.mcmods.devutil.common.ext.vanilla.data.toCompoundTag
import btpos.mcmods.devutil.common.ext.vanilla.isClientSide
import btpos.mcmods.devutil.common.ext.vanilla.plus
import btpos.mcmods.devutil.common.macros.ChatUtils.toComponent
import btpos.mcmods.devutil.forge.datagen.IItemDataGen
import btpos.mcmods.dungeondesignerlib.builder.redstone.items.ItemRemoteLinker.NbtAdapter
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraftforge.client.model.generators.ItemModelProvider
import org.spongepowered.asm.mixin.Mutable

/**
 * This is an item that allows you to link a transmitter to an arbitrary redstone component in the world
 * In reality it just adds that "thing" to the channel
 *
 * Use a chest on top of it like usual
 */
class ItemRemoteLinker(props: Properties) : Item(props) {
    companion object : IItemDataGen {
        fun getData(stack: ItemStack): NbtAdapter? {
            return stack.tag?.getCompoundOrNull("dungeondesigner")?.let(::NbtAdapter)
        }
        
        fun getOrCreateData(stack: ItemStack): NbtAdapter {
            return stack.getOrCreateTagElement("dungeondesigner").let(::NbtAdapter)
        }
        
        override fun ItemModelProvider.buildModels() {
            basicItem()
        }
        
        override val id: String
            get() = "remote_linker"
    }
    
    @JvmInline
    value class NbtAdapter(val tag: CompoundTag) {
        var target: BlockPos?
            get() = tag.getBlockPos("target")
            set(value) = tag.setOrRemove("target", value) { put("target", it.toCompoundTag()) }
    }
    
    override fun useOn(pContext: UseOnContext): InteractionResult {
        val player = pContext.player ?: return InteractionResult.PASS
        
        // Add target pos
        if (!pContext.isClientSide) {
            val targetPos = pContext.clickedPos
            getOrCreateData(pContext.itemInHand).target = targetPos
            player.sendSystemMessage("Added pos: ".asComponent() + targetPos.toComponent())
        }
        
        return InteractionResult.sidedSuccess(pContext.isClientSide)
    }
    
    override fun appendHoverText(pStack: ItemStack, pLevel: Level?, pTooltipComponents: MutableList<Component>, pIsAdvanced: TooltipFlag) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced)
        val pos = getData(pStack)?.target ?: return
        pTooltipComponents += "Target: ".asComponent() + pos.toComponent()
    }
}