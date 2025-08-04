package btpos.mcmods.devutil.common.registry

import btpos.mcmods.devutil.common.ext.kotlin.safeGetDelegate
import btpos.mcmods.dungeondesigner.MODID
import com.mojang.datafixers.types.Type
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

@Suppress("NonExtendableApiUsage")
class ObjectHolderDelegate<T>(val registryObject: RegistrySupplier<T>) : ReadOnlyProperty<Any?, T>, RegistrySupplier<T> by registryObject {
	override fun getValue(thisRef: Any?, property: KProperty<*>): T {
		return registryObject.get()
	}
	
	override fun getRegisteredName(): String? {
		return registryObject.getRegisteredName()
	}
}

fun <REG : Any, T : REG> DeferredRegister<REG>.registerObject(id: String, supplier: () -> T): ObjectHolderDelegate<T> {
	return ObjectHolderDelegate(this.register(id, supplier))
}

//region BlockEntityType Reflection
/**
 * Arch doesn't have [BlockEntityType] public, so this unreflects it and caches the method handle for performance.
 */
@JvmInline
private value class BlockEntityTypeConstructor private constructor(val handle: MethodHandle) {
	constructor() : this(Unit.run {
		val constructor = BlockEntityType::class.java.declaredConstructors.getOrNull(0)
		requireNotNull(constructor) { "Failed to get BlockEntityType constructor!!!" }
		constructor.isAccessible = true
		MethodHandles.lookup().unreflectConstructor(constructor)
	})
	
	@Suppress("UNCHECKED_CAST")
	operator fun <T : BlockEntity> invoke(factory: BlockEntityType.BlockEntitySupplier<T>, acceptedBlocks: Set<Block>): BlockEntityType<T> {
		return handle.invokeExact(factory, acceptedBlocks) as BlockEntityType<T>
	}
}
private val BETypeCtor: BlockEntityTypeConstructor by lazy { BlockEntityTypeConstructor() }
//endregion
/**
 * Use cached reflection to invoke constructor, since it's private.
 */
fun <T : BlockEntity> BlockEntityType(factory: (BlockPos, BlockState) -> T, vararg acceptedBlocks: Block): BlockEntityType<T> {
	return BETypeCtor(BlockEntityType.BlockEntitySupplier<T>(factory), acceptedBlocks.toSet())
}

typealias PlatformRegistry<T> = DeferredRegister<T>

interface IObjectRegistry<T : Any> {
	val REGISTRY: PlatformRegistry<T>
	
	fun <T> createRegistry(key: ResourceKey<Registry<T>>): PlatformRegistry<T> {
		return DeferredRegister.create(MODID, key)
	}
	
	fun register() {
		REGISTRY.register()
	}
	
	fun getId(prop: KProperty0<*>): ResourceLocation {
		prop.isAccessible = true
		
		return prop.safeGetDelegate<ObjectHolderDelegate<*>>()?.registryObject?.id
		       ?: throw IllegalStateException("Property $prop is not a registry delegate!")
	}
	
	fun <T : BlockEntity> PlatformRegistry<BlockEntityType<*>>.regBE(name: String, generator: () -> BlockEntityType<T>): ObjectHolderDelegate<BlockEntityType<T>> {
		return this.registerObject(name, generator)
	}
	
	fun registering(name: String, generator: () -> T): ObjectHolderDelegate<T> {
		return REGISTRY.registerObject(name, generator)
	}
	
	fun <T : Any> PlatformRegistry<DataComponentType<*>>.component(name: String, generator: () -> DataComponentType<T>): ObjectHolderDelegate<DataComponentType<T>> {
		return this.registerObject(name, generator)
	}
}

interface IBlockRegistry : IObjectRegistry<Block> {
	@Deprecated("Unused in IBlockRegistry")
	override val REGISTRY: PlatformRegistry<Block>
		get() = BLOCKS
	
	val BLOCKS: PlatformRegistry<Block>
	val ITEMS: PlatformRegistry<Item>
	val ENTITIES: PlatformRegistry<BlockEntityType<*>>
	
	override fun register() {
		BLOCKS.register()
		ITEMS.register()
		ENTITIES.register()
	}
	
	fun <T : Block> block(name: String,  withItem: Boolean = false, props: Item.Properties = Item.Properties(), supplier: () -> T): ObjectHolderDelegate<T> {
		return BLOCKS.registerObject(name, supplier).also {
			if (withItem)
				item(name) { BlockItem(it.get(), props) }
		}
	}
	
	fun <T : Item> item(bprop: KProperty0<*>, factory: () -> T): ObjectHolderDelegate<T> {
		val name = getId(bprop).path
		return item(name, factory)
	}
	
	fun <T : Item> item(name: String, supplier: () -> T): ObjectHolderDelegate<T> {
		return ITEMS.registerObject(name, supplier)
	}
	
	fun <B : Block> blockItem(bprop: KProperty0<B>, props: Item.Properties = Item.Properties()): ObjectHolderDelegate<BlockItem> {
		return item(bprop) { BlockItem(bprop.get(), props) }
	}
	
	fun <T : BlockEntity> ent(name: String, supplier: () -> BlockEntityType<T>): ObjectHolderDelegate<BlockEntityType<T>> {
		return ENTITIES.registerObject(name, supplier)
	}
	
	fun <B : Block, T : BlockEntity> ent(bprop: KProperty0<B>, supplier: (BlockPos, BlockState) -> T, type: Type<*>? = null): ObjectHolderDelegate<BlockEntityType<T>> {
		return ent(getId(bprop).path) {
			@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
			BlockEntityType(supplier, bprop.get())
		}
	}
	
	
	
	/* // you can't destructure in fields ;_;
	fun <B : Block, ITEM: BlockItem> registerBlock(name: String, blockSupplier: () -> B, itemSupplier: () -> ITEM): Pair<ObjectHolderDelegate<B>, ObjectHolderDelegate<ITEM>> {
		return Pair(BLOCKS.registerObject(name, blockSupplier), ITEMS.registerObject(name, itemSupplier))
	}
	
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