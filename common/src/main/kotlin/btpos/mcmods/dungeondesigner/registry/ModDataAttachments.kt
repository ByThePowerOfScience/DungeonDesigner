package btpos.mcmods.dungeondesigner.registry

import btpos.mcmods.devutil.common.registry.IObjectRegistry
import btpos.mcmods.devutil.common.registry.PlatformRegistry
import btpos.mcmods.dungeondesigner.builder.items.ItemTriggerVariable
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries

object ModDataAttachments : IObjectRegistry<DataComponentType<*>> {
	override val REGISTRY: PlatformRegistry<DataComponentType<*>> = createRegistry(Registries.DATA_COMPONENT_TYPE)
	
	val TRIGGER_VARIABLE_DATA: DataComponentType<ItemTriggerVariable.InternalData> by REGISTRY.component(ItemTriggerVariable.id) { DataComponentType.Builder<ItemTriggerVariable.InternalData>().persistent(ItemTriggerVariable.InternalData.CODEC).build() }
}