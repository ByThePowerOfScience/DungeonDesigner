package btpos.mcmods.devutil.forge.registry

import com.mojang.datafixers.types.Type
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.ObjectHolderDelegate
import thedarkcolour.kotlinforforge.forge.registerObject
import kotlin.reflect.KProperty0

interface IObjectRegistry<T> {
	val REGISTRY: DeferredRegister<T>
	
	fun register() {
		REGISTRY.register(MOD_BUS)
	}
	
	fun getId(prop: KProperty0<*>): ResourceLocation {
		return (prop.getDelegate() as? ObjectHolderDelegate<*>)?.registryObject?.id
		       ?: throw IllegalStateException("Property $prop is not a registry delegate!")
	}
	
	fun <T : BlockEntity> DeferredRegister<BlockEntityType<*>>.regBE(name: String, generator: () -> BlockEntityType<T>): ObjectHolderDelegate<BlockEntityType<T>> {
		return this.registerObject(name, generator)
	}
}

interface IBlockRegistry : IObjectRegistry<Block> {
	override val REGISTRY: DeferredRegister<Block>
		get() = BLOCKS
	
	val BLOCKS: DeferredRegister<Block>
	val ITEMS: DeferredRegister<Item>
	val ENTITIES: DeferredRegister<BlockEntityType<*>>
	
	
	override fun register() {
		BLOCKS.register(MOD_BUS)
		ITEMS.register(MOD_BUS)
		ENTITIES.register(MOD_BUS)
	}
	
	fun <T : Block> block(name: String, supplier: () -> T): ObjectHolderDelegate<T> {
		return BLOCKS.registerObject(name, supplier)
	}
	
	fun <T : Item> item(name: String, supplier: () -> T): ObjectHolderDelegate<T> {
		return ITEMS.registerObject(name, supplier)
	}
	
	fun <B : Block> item(bprop: KProperty0<B>, props: Item.Properties = Item.Properties()): ObjectHolderDelegate<BlockItem> {
		val name = getId(bprop).path
		
		return item(name) { BlockItem(bprop.get(), props) }
	}
	
	fun <T : BlockEntity> ent(name: String, supplier: () -> BlockEntityType<T>): ObjectHolderDelegate<BlockEntityType<T>> {
		return ENTITIES.registerObject(name, supplier)
	}
	
	fun <B : Block, T : BlockEntity> ent(bprop: KProperty0<B>, supplier: (BlockPos, BlockState) -> T, type: Type<*>? = null): ObjectHolderDelegate<BlockEntityType<T>> {
		return ent(getId(bprop).path) {
			@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
			BlockEntityType.Builder.of(supplier, bprop.get()).build(type)
		}
	}
	
	
	
	/*
	fun <B : Block, ITEM: BlockItem> registerBlock(name: String, blockSupplier: () -> B, itemSupplier: () -> ITEM): Pair<ObjectHolderDelegate<B>, ObjectHolderDelegate<ITEM>> {
		return Pair(BLOCKS.registerObject(name, blockSupplier), ITEMS.registerObject(name, itemSupplier))
	}
	
	// you can't destructure in fields ;_;
	fun <B : Block, ENT : BlockEntity> registerBlock(name: String, blockSupplier: () -> B, entFactory: (BlockPos, BlockState) -> ENT, dataType: Type<*>? = null): Triple<ObjectHolderDelegate<B>, ObjectHolderDelegate<BlockItem>, ObjectHolderDelegate<BlockEntityType<ENT>>> {
		val block = BLOCKS.registerObject(name, blockSupplier)
		val entDelegate = ENTITIES.registerObject(name) {
			@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
			BlockEntityType.Builder.of(entFactory, block.get()).build(dataType)
		}
		return Triple(block, ITEMS.registerObject(name, {
			BlockItem(block.get(), Item.Properties())
		}), entDelegate)
	}
	
	fun <B : Block, ENT : BlockEntity, ITEM : Item> registerBlock(name: String, blockSupplier: () -> B, itemSupplier: () -> ITEM, entFactory: (BlockPos, BlockState) -> ENT, dataType: Type<*>? = null): Triple<ObjectHolderDelegate<B>, ObjectHolderDelegate<ITEM>, ObjectHolderDelegate<BlockEntityType<ENT>>> {
		val block = BLOCKS.registerObject(name, blockSupplier)
		val entDelegate = ENTITIES.registerObject(name) {
			@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
			BlockEntityType.Builder.of(entFactory, block.get()).build(dataType)
		}
		return Triple(block, ITEMS.registerObject(name, itemSupplier), entDelegate)
	}
	*/
	
	
}