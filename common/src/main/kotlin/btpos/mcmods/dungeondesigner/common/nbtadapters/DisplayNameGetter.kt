package btpos.mcmods.dungeondesigner.common.nbtadapters

import btpos.mcmods.devutil.common.ext.vanilla.asComponent
import btpos.mcmods.devutil.common.ext.vanilla.data.getCompoundOrNull
import btpos.mcmods.devutil.common.ext.vanilla.data.getOrCreateCompound
import btpos.mcmods.devutil.common.ext.vanilla.data.getStringOrNull
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

// Deprecated since 1.20.1
///**
// * Adapter to get the display name from an ItemStack's root CompoundTag.
// */
//@JvmInline
//value class DisplayNameGetter(val tag: CompoundTag) {
//    var nameJson: String?
//        get() = tag.getCompoundOrNull(ItemStack.TAG_DISPLAY)?.getStringOrNull(ItemStack.TAG_DISPLAY_NAME)
//        set(value) {
//            if (value == null) {
//                tag.getCompoundOrNull(ItemStack.TAG_DISPLAY)?.remove(ItemStack.TAG_DISPLAY_NAME)
//                return;
//            }
//            else {
//                tag.getOrCreateCompound(ItemStack.TAG_DISPLAY).putString(ItemStack.TAG_DISPLAY_NAME, value)
//            }
//        }
//}
// 1.20.1
//fun getDisplayName(stack: ItemStack): String? {
//    return stack.tag?.let(::DisplayNameGetter)?.nameJson
//}

fun getDisplayName(stack: ItemStack): String? {
    return stack.components[DataComponents.CUSTOM_NAME]?.string
}

fun setDisplayName(stack: ItemStack, name: String) {
    stack.set(DataComponents.CUSTOM_NAME, Component.literal(name))
}