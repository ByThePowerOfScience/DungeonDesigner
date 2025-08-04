@file:Suppress("OVERRIDE_DEPRECATION")

package btpos.mcmods.dungeondesigner.builder.items

import btpos.mcmods.devutil.common.ext.java.invoke
import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.ext.vanilla.data.getCompoundOrNull
import btpos.mcmods.devutil.common.ext.vanilla.data.getOrCreateCompound
import btpos.mcmods.devutil.common.ext.vanilla.data.nullableFieldOf
import btpos.mcmods.devutil.common.ext.vanilla.plus
import btpos.mcmods.devutil.common.ext.vanilla.sendSystemMessage
import btpos.mcmods.devutil.common.ext.vanilla.world.runOnServer
import btpos.mcmods.devutil.common.macros.ChatUtils.toComponent
import btpos.mcmods.devutil.common.structure.blocks.IObjectData
import btpos.mcmods.dungeondesigner.registry.ModDataAttachments
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import java.util.function.Consumer

/**
 * Draws a trigger in the world.
 *
 * NBT: {
 *      "trigger_brush": [TriggerBounds]
 * }
 */
class ItemTriggerVariable(props: Properties) : Item(props) {
	companion object : IObjectData {
		override val id: String
			get() = "trigger_variable"
		
		// NBT Tag Keys
		const val TAGKEY_STATE = "trigger_brush"
		
//		override fun ItemModelProvider.buildModels() {
//			this.basicItem()
//		}
		
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
		
		
		fun getData(stack: ItemStack): InternalData? {
			return stack.get(ModDataAttachments.TRIGGER_VARIABLE_DATA)
//			return stack.tag?.let(::getTriggerBoundsNbt)?.let(::NbtAdapter)
		}
		
		fun getOrCreateData(stack: ItemStack): InternalData {
			return stack.get(ModDataAttachments.TRIGGER_VARIABLE_DATA) ?: run {
				InternalData().also {
					stack.set(ModDataAttachments.TRIGGER_VARIABLE_DATA, it)
				}
			}
//			return getOrCreateTriggerNbt(stack.orCreateTag).let(::NbtAdapter)
		}
	}
	
	
	
	override fun useOn(ctx: UseOnContext): InteractionResult {
		return whenUsedOnBlock(ctx)
	}
	
	fun whenUsedOnBlock(ctx: UseOnContext): InteractionResult {
		val player = ctx.player ?: return InteractionResult.FAIL
		
		return ctx.level.runOnServer {
			if (!player.isShiftKeyDown) {
				getOrCreateData(ctx.itemInHand).first = ctx.clickedPos
				player.sendSystemMessage(
					Component.literal("Set first corner to ")
						.append(ctx.clickedPos.toComponent().withStyle(ChatFormatting.YELLOW))
				)
			} else {
				getOrCreateData(ctx.itemInHand).second = ctx.clickedPos
				player.sendSystemMessage(
					Component.literal("Set second corner to ")
						.append(ctx.clickedPos.toComponent().withStyle(ChatFormatting.YELLOW))
				)
			}
		}.sidedResult
	}
	
	override fun appendHoverText(pStack: ItemStack, context: TooltipContext, tooltipDisplay: TooltipDisplay, tooltipAdder: Consumer<Component>, pIsAdvanced: TooltipFlag) {
		super.appendHoverText(pStack, context, tooltipDisplay, tooltipAdder, pIsAdvanced)
		getData(pStack)?.run {
			first?.let { tooltipAdder("First corner: ".asComponent() + it.toComponent()) }
			second?.let { tooltipAdder("Second corner: ".asComponent() + it.toComponent()) }
		}
	}
	
//	/**
//	 * Wrapper giving managed structural access to a CompoundTag, because Items + Codecs = unfun.
//	 */
//	@JvmInline
//	value class NbtAdapter(val tag: CompoundTag) : InternalData {
//		override var first: BlockPos?
//			get() = tag.getBlockPos("first")
//			set(value) {
//				if (value == null)
//					tag.remove("first")
//				else
//					tag.put("first", value.toCompoundTag())
//			}
//
//		override var second: BlockPos?
//			get() = tag.getBlockPos("second")
//			set(value) {
//				if (value == null)
//					tag.remove("second")
//				else
//					tag.put("second", value.toCompoundTag())
//			}
//	}
	
	interface InternalData {
		var first: BlockPos?
		var second: BlockPos?
		
		fun isComplete(): Boolean {
			return first != null && second != null
		}
		
		
		companion object {
			val CODEC: Codec<InternalData> = RecordCodecBuilder.create {
				it.group(
						BlockPos.CODEC.nullableFieldOf("first").forGetter(InternalData::first),
						BlockPos.CODEC.nullableFieldOf("second").forGetter(InternalData::second),
				).apply(it, ::Impl)
			}
			
			operator fun invoke(first: BlockPos? = null, second: BlockPos? = null): InternalData {
				return Impl(first, second)
			}
		}
		data class Impl(override var first: BlockPos? = null, override var second: BlockPos? = null) : InternalData
	}
}
