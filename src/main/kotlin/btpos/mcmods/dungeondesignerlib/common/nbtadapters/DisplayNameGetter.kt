package btpos.mcmods.dungeondesignerlib.common.nbtadapters

import btpos.mcmods.devutil.common.ext.vanilla.data.getCompoundOrNull
import btpos.mcmods.devutil.common.ext.vanilla.data.getOrCreateCompound
import btpos.mcmods.devutil.common.ext.vanilla.data.getStringOrNull
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack

/**
 * Adapter to get the display name from an ItemStack's root CompoundTag.
 */
@JvmInline
value class DisplayNameGetter(val tag: CompoundTag) {
    var nameJson: String?
        get() = tag.getCompoundOrNull(ItemStack.TAG_DISPLAY)?.getStringOrNull(ItemStack.TAG_DISPLAY_NAME)
        set(value) {
            if (value == null) {
                tag.getCompoundOrNull(ItemStack.TAG_DISPLAY)?.remove(ItemStack.TAG_DISPLAY_NAME)
                return;
            }
            else {
                tag.getOrCreateCompound(ItemStack.TAG_DISPLAY).putString(ItemStack.TAG_DISPLAY_NAME, value)
            }
        }
}