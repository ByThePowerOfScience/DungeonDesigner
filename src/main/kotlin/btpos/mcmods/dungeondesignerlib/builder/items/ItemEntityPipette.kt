package btpos.mcmods.dungeondesignerlib.builder.items

import btpos.mcmods.devutil.common.ext.vanilla.data.getOrCreateCompound
import btpos.mcmods.devutil.common.ext.vanilla.world.holder
import btpos.mcmods.devutil.forge.datagen.IItemDataGen
import btpos.mcmods.dungeondesignerlib.builder.nbt.PipetteData
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraftforge.client.model.generators.ItemModelProvider

/**
 * Holds a spawn egg and a spawn position. Used to configure the FightController.
 *
 * @see btpos.mcmods.dungeondesignerlib.builder.blocks.actors.BlockFightController
 */
class ItemEntityPipette(pProps: Properties) : Item(pProps) {
	companion object : IItemDataGen {
		override val id: String
			get() = "entity_pipette"
		
		override fun ItemModelProvider.buildModels() {
			basicItem()
		}
	}
	
	override fun useOn(pContext: UseOnContext): InteractionResult {
		val heldStack = pContext.itemInHand
		
		PipetteData.NbtAdapter(heldStack.getOrCreateTag()).spawnPos = pContext.clickedPos
		
		return InteractionResult.CONSUME
	}
	
	override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		val heldStack = pPlayer.getItemInHand(pUsedHand)
		
		if (pLevel.isClientSide && pPlayer.isShiftKeyDown) {
			
			TODO("Open GUI to put in the spawn egg")
		}
		
		return InteractionResultHolder.sidedSuccess(heldStack, pLevel.isClientSide)
	}
}