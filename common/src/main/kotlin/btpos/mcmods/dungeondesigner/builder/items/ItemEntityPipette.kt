package btpos.mcmods.dungeondesigner.builder.items


import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.ext.vanilla.plus
import btpos.mcmods.devutil.common.ext.vanilla.sendSystemMessage
import btpos.mcmods.devutil.common.ext.vanilla.world.sidedSuccess
import btpos.mcmods.devutil.common.macros.ChatUtils.toComponent
import btpos.mcmods.devutil.common.structure.blocks.IObjectData
import btpos.mcmods.devutil.common.util.EntityUtils.getTargetedEntity
import btpos.mcmods.dungeondesigner.builder.nbt.IEntitySpawnData
import btpos.mcmods.dungeondesigner.registry.ModItems
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.ProblemReporter
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.TagValueOutput
import net.minecraft.world.level.storage.ValueOutput

/**
 * Holds a spawn egg and a spawn position. Used to configure the FightController.
 *
 * @see btpos.mcmods.dungeondesigner.builder.blocks.actors.BlockFightController
 * @see IEntitySpawnData.AsTag
 */
class ItemEntityPipette(pProps: Properties) : Item(pProps) {
	companion object : IObjectData {
		override val id: String
			get() = "entity_pipette"
//
//		override fun ItemModelProvider.buildModels() {
//			basicItem()
//		}
		
		const val TAGKEY_DATA = "spawndata"
		
		fun isReadyToSpawn(data: IEntitySpawnData): Boolean {
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
		
		fun getDataOrNull(stack: ItemStack): IEntitySpawnData? {
			if (stack.item != ModItems.PIPETTE_ITEM)
				return null
			
//			val data = stack.getTagElement(TAGKEY_DATA) ?: return null
			
			return IEntitySpawnData.AsTag(data)
		}
		
		fun getOrCreateData(stack: ItemStack): IEntitySpawnData {
			if (stack.item != ModItems.PIPETTE_ITEM)
				btpos.mcmods.dungeondesigner.MOD_LOGGER.warn("Expected item {}, got {}", resourceLocation, ForgeRegistries.ITEMS.getKey(stack.item))
			
//			return IEntitySpawnData.AsTag(stack.getOrCreateTagElement(TAGKEY_DATA))
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
//			getOrCreateData(newStack).run {
//				pos = pContext.clickedPos
//				rotation = -(pContext.player?.yRot ?: 0f)
//
//				pContext.player?.sendSystemMessage("Spawn position: ".asComponent() + pos.toComponent().withStyle(ChatFormatting.YELLOW) + " with rotation $rotation degrees.")
//			}
			if (shouldSplitOff) {
				pContext.player?.addItem(newStack)
			}
		}
		
		return sidedSuccess(pContext.level.isClientSide)
	}
	
	/**
	 * Save the looked-at entity to this item's NBT.
	 *
	 * @see net.minecraft.client.KeyboardHandler.copyCreateEntityCommand
	 * @see net.minecraft.world.entity.Entity.saveWithoutId
	 * @see net.minecraft.server.network.ServerGamePacketListenerImpl.handleEntityTagQuery
	 */
	override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResult {
		val heldStack = pPlayer.getItemInHand(pUsedHand)
		
		val lookedAtEntity = pPlayer.getTargetedEntity(10.0) ?: return InteractionResult.PASS
		if (lookedAtEntity !is LivingEntity)
			return InteractionResult.PASS
		
		return pLevel.runOnServer {
			val tag = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING).also {
				lookedAtEntity.saveWithoutId(it)
				it.apply {
					remove("UUID");
					remove("Pos");
					remove("Dimension");
				}
			}.buildResult()
			
			val type = lookedAtEntity.type
			
			getOrCreateData(heldStack).run {
				this.nbt = tag
				@Suppress("UNCHECKED_CAST")
				this.type = type as EntityType<LivingEntity>
			}
			
			pPlayer.sendSystemMessage("Saved ${BuiltInRegistries.ENTITY_TYPE.getKey(type)} to pipette.".asComponent())
		}.sidedResult
	}
	
	override fun appendHoverText(pStack: ItemStack, pLevel: Level?, pTooltipComponents: MutableList<Component>, pIsAdvanced: TooltipFlag) {
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced)
		val data = getDataOrNull(pStack) ?: return
		with (pTooltipComponents) {
			with (data) {
				type?.let { add("Mob: ${ForgeRegistries.ENTITY_TYPES.getKey(type)}".asComponent()) }
				pos?.let { add("Position: ".asComponent() + it.toComponent()) }
				rotation?.let { add("Rotation: $it".asComponent()) }
			}
		}
	}
}