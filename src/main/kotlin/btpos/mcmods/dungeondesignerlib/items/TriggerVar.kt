package btpos.mcmods.dungeondesignerlib.items

import btpos.mcmods.devutil.common.util.ChatUtils
import btpos.mcmods.devutil.forge.datagen.IItemDataGen
import btpos.mcmods.dungeondesignerlib.saveddata.TriggerBoundsTag
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraftforge.client.model.generators.ItemModelProvider

/**
 * Draws a trigger in the world.
 *
 * NBT: {
 *      "trigger_brush": [TriggerBounds]
 * }
 */
class ItemTriggerVariable(props: Properties) : Item(props) {
	companion object : IItemDataGen {
		override val id: String
			get() = "trigger_variable"
		
		// NBT Tag Keys
		const val STATE = "trigger_brush"
		
		override fun ItemModelProvider.buildModels() {
			this.basicItem()
		}
	}
	
	override fun useOn(ctx: UseOnContext): InteractionResult {
		return whenUsedOnBlock(ctx)
	}
	
	fun whenUsedOnBlock(ctx: UseOnContext): InteractionResult {
		val player = ctx.player ?: return InteractionResult.FAIL
		if (ctx.level.isClientSide)
			return InteractionResult.sidedSuccess(true)
		
		if (!player.isShiftKeyDown) {
			ctx.itemInHand.getTriggerBounds().first = ctx.clickedPos
			player.sendSystemMessage(Component.literal("Set first corner to ").append(ChatUtils.toComponent(ctx.clickedPos)))
		} else {
			ctx.itemInHand.getTriggerBounds().second = ctx.clickedPos
			
			player.sendSystemMessage(Component.literal("Set second corner to ").append(ChatUtils.toComponent(ctx.clickedPos)))
		}
		
		return InteractionResult.SUCCESS
	}
	
	fun ItemStack.getTriggerBounds(): TriggerBoundsTag {
		return TriggerBoundsTag(this.getOrCreateTagElement(STATE))
	}
}

