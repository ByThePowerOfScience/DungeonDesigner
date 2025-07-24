package btpos.mcmods.dungeondesignerlib.builder.items


import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.ext.vanilla.plus
import btpos.mcmods.devutil.common.macros.ChatUtils.toComponent
import btpos.mcmods.devutil.common.util.EntityUtils.getTargetedEntity
import btpos.mcmods.devutil.forge.datagen.IItemDataGen
import btpos.mcmods.dungeondesignerlib.builder.nbt.IEntitySpawnData
import btpos.mcmods.dungeondesignerlib.registry.ModItems
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.registries.ForgeRegistries

/**
 * Holds a spawn egg and a spawn position. Used to configure the FightController.
 *
 * @see btpos.mcmods.dungeondesignerlib.builder.blocks.actors.BlockFightController
 * @see IEntitySpawnData.AsTag
 */
class ItemEntityPipette(pProps: Properties) : Item(pProps) {
	companion object : IItemDataGen {
		override val id: String
			get() = "entity_pipette"
		
		override fun ItemModelProvider.buildModels() {
			basicItem()
		}
		
		const val TAGKEY_DATA = "spawndata"
		
		fun isReadyToSpawn(data: IEntitySpawnData.AsTag): Boolean {
			return data.run { pos != null && type != null && rotation != null }
		}
		
		/**
		 * Returns true if this itemstack both is a pipette and has enough information to spawn a mob.
		 */
		fun isReadyToSpawn(stack: ItemStack): Boolean {
			if (stack.item != this) {
				return false
			}
			return getDataOrNull(stack)?.let(::isReadyToSpawn) ?: false
		}
		
		fun getDataOrNull(stack: ItemStack): IEntitySpawnData.AsTag? {
			if (stack.item != ModItems.PIPETTE_ITEM)
				return null
			
			val data = stack.getTagElement(TAGKEY_DATA) ?: return null
			
			return IEntitySpawnData.AsTag(data)
		}
		
		fun getOrCreateData(stack: ItemStack): IEntitySpawnData.AsTag {
			if (stack.item != ModItems.PIPETTE_ITEM)
				btpos.mcmods.dungeondesignerlib.MOD_LOGGER.warn("Expected item {}, got {}", resourceLocation, ForgeRegistries.ITEMS.getKey(stack.item))
			
			return IEntitySpawnData.AsTag(stack.getOrCreateTagElement(TAGKEY_DATA))
		}
	}
	
	/**
	 * Use on block, save position AND ROTATION to nbt.
	 */
	override fun useOn(pContext: UseOnContext): InteractionResult {
		val heldStack = pContext.itemInHand
		
		
		
		if (!pContext.level.isClientSide){
			// split one off
			val shouldSplitOff = heldStack.count > 1
			val newStack = if (shouldSplitOff) heldStack.split(1) else heldStack
			getOrCreateData(newStack).run {
				pos = pContext.clickedPos
				rotation = -(pContext.player?.xRot ?: 0f)
				
				pContext.player?.sendSystemMessage("Spawn position: ".asComponent() + pos.toComponent().withStyle(ChatFormatting.YELLOW) + " with rotation $rotation degrees.")
			}
			if (shouldSplitOff)
				pContext.player?.addItem(newStack)
		}
		
		return InteractionResult.sidedSuccess(pContext.level.isClientSide)
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
			
			getOrCreateData(heldStack).run {
				this.nbt = tag
				@Suppress("UNCHECKED_CAST")
				this.type = type as EntityType<LivingEntity>
			}
			
			pPlayer.sendSystemMessage("Saved ${ForgeRegistries.ENTITY_TYPES.getKey(type)} to pipette.".asComponent())
		}
		
		return InteractionResultHolder.sidedSuccess(heldStack, pLevel.isClientSide)
	}
}