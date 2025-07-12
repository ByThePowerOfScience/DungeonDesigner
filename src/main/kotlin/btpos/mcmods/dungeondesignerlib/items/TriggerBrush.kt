package btpos.mcmods.dungeondesignerlib.items

import btpos.mcmods.devutil.common.ext.vanilla.blockEntity
import btpos.mcmods.devutil.common.ext.vanilla.getBlockPos
import btpos.mcmods.devutil.common.ext.vanilla.targetBlockState
import btpos.mcmods.devutil.common.ext.vanilla.toCompoundTag
import btpos.mcmods.dungeondesignerlib.blocks.TileDungeonNexus
import btpos.mcmods.dungeondesignerlib.registry.ModBlocks
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level

/**
 * Draws a trigger in the world.
 */
class ItemTriggerBrush(props: Properties) : Item(props) {
	companion object TagKeys {
		const val STATE = "trigger_brush"
		const val NEXUS_LOCATION = "nexus"
		const val FIRST_CORNER = "first"
		const val SECOND_CORNER = "second"
	}
	
	override fun onItemUseFirst(stack: ItemStack, context: UseOnContext): InteractionResult {
		return super.onItemUseFirst(stack, context)
	}
	
	override fun useOn(ctx: UseOnContext): InteractionResult {
		return whenUsedOnBlock(ctx)
	}
	
	fun whenUsedOnBlock(ctx: UseOnContext): InteractionResult {
		val player = ctx.player ?: return InteractionResult.FAIL
		
		if (player.isShiftKeyDown) {
			if (ctx.targetBlockState.block == ModBlocks.DUNGEON_NEXUS) {
				return registerNexus(
						ctx.itemInHand,
						ctx.level.getBlockEntity(ctx.clickedPos) as? TileDungeonNexus ?: return InteractionResult.FAIL
				)
			} else {
				ctx.itemInHand.getOurState().put(SECOND_CORNER, ctx.clickedPos.toCompoundTag())
				return InteractionResult.SUCCESS
			}
		} else {
			ctx.itemInHand.getOurState().put(FIRST_CORNER, ctx.clickedPos.toCompoundTag())
			return InteractionResult.SUCCESS
		}
	}
	
	fun registerNexus(item: ItemStack, ent: TileDungeonNexus): InteractionResult {
		item.getOurState().put(NEXUS_LOCATION, ent.blockPos.toCompoundTag())
		return InteractionResult.SUCCESS
	}
	
	fun ItemStack.getOurState(): CompoundTag {
		return this.getOrCreateTagElement(STATE)
	}
	
	fun ItemStack.getTargetNexus(level: Level): TileDungeonNexus? {
		return this.getOrCreateTagElement(STATE).getBlockPos(NEXUS_LOCATION)?.let {
			level.blockEntity(it, ModBlocks.DUNGEON_NEXUS_ENTITY)
		}
	}
}

