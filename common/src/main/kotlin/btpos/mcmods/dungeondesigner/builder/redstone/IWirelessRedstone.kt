package btpos.mcmods.dungeondesigner.builder.redstone

import btpos.mcmods.devutil.common.structure.composition.IOnChange
import btpos.mcmods.devutil.common.util.serialization.ICodecSerializableMutable
import btpos.mcmods.dungeondesigner.builder.world.dungeonBuilderData
import btpos.mcmods.dungeondesigner.registry.ModItemComponents
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack

interface IWirelessRedstone {
    val channel: Int
    
//    @JvmInline
//    value class NbtAdapter(val tag: CompoundTag) : IWirelessRedstone {
//        override var channel: Int
//            get() = (tag.get("channel") as NumericTag?)?.asInt()?.getOrNull() ?: NO_CHANNEL
//            set(value) {
//                tag.putInt("channel", value)
//            }
//    }
    
    
    /**
     * Mutable variant for tile entities.
     */
    class Mutable(pChannel: Int, override var onChange: () -> Unit = {}) : ICodecSerializableMutable<Mutable>, IWirelessRedstone, IOnChange {
        constructor(immutable: IWirelessRedstone) : this(immutable.channel)
        
        override var channel: Int by notify(pChannel)
        
        companion object {
            @JvmField
            val MUTABLE_CODEC = IMMUTABLE_CODEC.xmap({ Mutable(it.channel) }, { it })
        }
        
        override fun codec() = MUTABLE_CODEC
        
        override fun copyFrom(other: Mutable) {
            this.channel = other.channel
        }
    }
    
    companion object {
        val IMMUTABLE_CODEC = RecordCodecBuilder.create {
            it.group(
                    Codec.INT.fieldOf("channel").forGetter(IWirelessRedstone::channel)
            ).apply(it, ::make)
        }
        
        /**
         * Default implementation of [IWirelessRedstone]
         */
        fun make(channel: Int): IWirelessRedstone {
            data class Impl(override val channel: Int) : IWirelessRedstone
            return Impl(channel)
        }
        
        const val NO_CHANNEL = -1
        
        fun getFromStack(stack: ItemStack): IWirelessRedstone? {
            return stack.get(ModItemComponents.WIRELESS_REDSTONE)
        }
        
        fun setOnStack(stack: ItemStack, value: IWirelessRedstone?) {
            stack.set(ModItemComponents.WIRELESS_REDSTONE, value)
        }
        
        /**
         * Since components are immutable by default,
         * I'd rather abstract the whole "get and set component" thing to its own method
         * so we don't have to completely rip it out if we swap back to CompoundTags
         * or they rework the components system.
         */
        inline fun modifyData(stack: ItemStack, transformer: (Mutable) -> Unit) {
            val toModify = getFromStack(stack)?.let(::Mutable) ?: Mutable(NO_CHANNEL)
            transformer(toModify)
            setOnStack(stack, toModify)
        }
        
    }
}

interface IWirelessRedstoneTransmitter : IWirelessRedstone {
    /**
     * Call when your block starts receiving redstone power
     */
    fun onStartReceivingSignal(level: ServerLevel) {
        level.dataStorage.dungeonBuilderData.redstoneHandler.powerChannel(this.channel, level)
    }
    
    fun onStopReceivingSignal(level: ServerLevel) {
        level.dataStorage.dungeonBuilderData.redstoneHandler.unpowerChannel(this.channel, level)
    }
    
    companion object {
        fun make(channel: Int): IWirelessRedstoneTransmitter {
            data class Impl(override val channel: Int) : IWirelessRedstoneTransmitter
            return Impl(channel)
        }
    }
}

