package btpos.mcmods.dungeondesignerlib.builder.items

import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.ext.vanilla.data.getCompoundOrNull
import btpos.mcmods.devutil.common.ext.vanilla.data.getOrCreateCompound
import btpos.mcmods.devutil.common.ext.vanilla.plus
import btpos.mcmods.devutil.common.macros.ChatUtils.toComponent
import btpos.mcmods.devutil.forge.datagen.IItemDataGen
import btpos.mcmods.dungeondesignerlib.builder.nbt.TriggerBoundsTag
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
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
		
		/**
		 * Gets the trigger bounds section from the root tag of one of our itemstacks. Returns null if the subtag is absent.
		 */
        fun getTriggerBoundsNbt(tag: CompoundTag): CompoundTag? {
			return tag.getCompoundOrNull(TAGKEY_STATE)
		}
		/**
		 * Gets or creates the trigger bounds section from the root tag of one of our itemstacks.
		 */
		fun getOrCreateTriggerNbt(tag: CompoundTag): CompoundTag {
			return tag.getOrCreateCompound(TAGKEY_STATE)
		}
		
		fun getData(stack: ItemStack): TriggerBoundsTag? {
			return stack.tag?.let(::getTriggerBoundsNbt)?.let(::TriggerBoundsTag)
		}
		
		fun getOrCreateData(stack: ItemStack): TriggerBoundsTag {
			return getOrCreateTriggerNbt(stack.orCreateTag).let(::TriggerBoundsTag)
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
			getOrCreateData(ctx.itemInHand).first = ctx.clickedPos
			player.sendSystemMessage(Component.literal("Set first corner to ").append(ctx.clickedPos.toComponent().withStyle(ChatFormatting.YELLOW)))
		} else {
			getOrCreateData(ctx.itemInHand).second = ctx.clickedPos
			player.sendSystemMessage(Component.literal("Set second corner to ").append(ctx.clickedPos.toComponent().withStyle(ChatFormatting.YELLOW)))
		}
		
		return InteractionResult.CONSUME
	}
	
	override fun appendHoverText(
		pStack: ItemStack,
		pLevel: Level?,
		pTooltipComponents: MutableList<Component>,
		pIsAdvanced: TooltipFlag
	) {
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced)
		getData(pStack)?.run {
			first?.let { pTooltipComponents += "First corner: ".asComponent() + it.toComponent() }
			second?.let { pTooltipComponents += "Second corner: ".asComponent() + it.toComponent() }
		}
	}
}

