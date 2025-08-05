package btpos.mcmods.dungeondesigner.registry

import btpos.mcmods.devutil.common.registry.IDataComponentRegistry
import btpos.mcmods.devutil.common.registry.PlatformRegistry
import btpos.mcmods.dungeondesigner.builder.items.ItemTriggerVariable
import btpos.mcmods.dungeondesigner.builder.nbt.IEntitySpawnData
import btpos.mcmods.dungeondesigner.builder.redstone.IWirelessRedstone
import btpos.mcmods.dungeondesigner.builder.redstone.items.ItemRemoteLinker
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries

object ModItemComponents : IDataComponentRegistry<DataComponentType<*>> {
	override val REGISTRY: PlatformRegistry<DataComponentType<*>> = createRegistry(Registries.DATA_COMPONENT_TYPE)
	
	val TRIGGER_VARIABLE_DATA: DataComponentType<ItemTriggerVariable.InternalData> by REGISTRY.component(ItemTriggerVariable.id) { buildPersistentComponent(ItemTriggerVariable.InternalData.CODEC) }
	
	val WIRELESS_REDSTONE: DataComponentType<IWirelessRedstone> by REGISTRY.component("builder_wireless_redstone") { buildPersistentComponent(IWirelessRedstone.IMMUTABLE_CODEC, cacheEncoding=true) }
	
	val ENTITY_PIPETTE by REGISTRY.component("entity_pipette") { buildPersistentComponent(IEntitySpawnData.CODEC) }
	
	val REMOTE_LINKER by REGISTRY.component("remote_linker") { buildPersistentComponent(ItemRemoteLinker.InternalData.CODEC) }
}