package btpos.mcmods.dungeondesignerlib.builder.items


import btpos.mcmods.devutil.common.util.EntityUtils
import btpos.mcmods.devutil.common.util.EntityUtils.getTargetedEntity
import btpos.mcmods.devutil.forge.datagen.IItemDataGen
import btpos.mcmods.dungeondesignerlib.builder.nbt.IEntityFightData
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraftforge.client.model.generators.ItemModelProvider
import thedarkcolour.kotlinforforge.forge.vectorutil.v2d.unaryMinus

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
		
		const val TAGKEY_DATA = "spawndata"
	}
	
	/**
	 * Use on block, save position AND ROTATION to nbt.
	 */
	override fun useOn(pContext: UseOnContext): InteractionResult {
		val heldStack = pContext.itemInHand
		
		heldStack.getOrCreateTagElement(TAGKEY_DATA).let(IEntityFightData::NbtAdapter).run {
			pos = pContext.clickedPos
			rotation = -(pContext.player?.xRot ?: 0f)
		}
		
		return InteractionResult.CONSUME
	}
	
	/**
	 * Save the looked-at entity to this item's NBT.
	 *
	 * @see net.minecraft.client.KeyboardHandler.copyCreateEntityCommand
	 * @see net.minecraft.world.entity.Entity.saveWithoutId
	 * @see net.minecraft.server.network.ServerGamePacketListenerImpl.handleEntityTagQuery
	 */
	override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		val heldStack = pPlayer.getItemInHand(pUsedHand)
		
		val lookedAtEntity = pPlayer.getTargetedEntity(10.0) ?: return InteractionResultHolder.pass(heldStack)
		if (lookedAtEntity !is LivingEntity)
			return InteractionResultHolder.pass(heldStack)
		
		if (!pLevel.isClientSide) {
			val tag = lookedAtEntity.saveWithoutId(CompoundTag()).apply {
				remove("UUID");
				remove("Pos");
				remove("Dimension");
			}
			
			val type = lookedAtEntity.type
			
			heldStack.getOrCreateTagElement(TAGKEY_DATA).let(IEntityFightData::NbtAdapter).run {
				this.nbt = tag
				@Suppress("UNCHECKED_CAST")
				this.type = type as EntityType<LivingEntity>
			}
		}
		
		return InteractionResultHolder.sidedSuccess(heldStack, pLevel.isClientSide)
	}
}