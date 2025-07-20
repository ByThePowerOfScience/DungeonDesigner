package btpos.mcmods.dungeondesignerlib.builder.items

import btpos.mcmods.devutil.common.macros.ChatUtils.toComponent
import btpos.mcmods.devutil.forge.datagen.IItemDataGen
import btpos.mcmods.dungeondesignerlib.builder.nbt.TriggerBoundsTag
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
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
		const val TAGKEY_STATE = "trigger_brush"
		
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
			return InteractionResult.SUCCESS
		
		if (!player.isShiftKeyDown) {
			ctx.itemInHand.getTriggerBounds().first = ctx.clickedPos
			player.sendSystemMessage(Component.literal("Set first corner to ").append(ctx.clickedPos.toComponent().withStyle(ChatFormatting.YELLOW)))
		} else {
			ctx.itemInHand.getTriggerBounds().second = ctx.clickedPos
			player.sendSystemMessage(Component.literal("Set second corner to ").append(ctx.clickedPos.toComponent().withStyle(ChatFormatting.YELLOW)))
		}
		
		return InteractionResult.CONSUME
	}
	
	@Suppress("NOTHING_TO_INLINE")
	inline fun ItemStack.getTriggerBounds(): TriggerBoundsTag {
		return TriggerBoundsTag(this.getOrCreateTagElement(TAGKEY_STATE))
	}
}

