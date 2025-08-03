package btpos.mcmods.dungeondesignerlib.builder.redstone

import btpos.mcmods.dungeondesignerlib.builder.world.dungeonBuilderData
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NumericTag
import net.minecraft.server.level.ServerLevel

interface IWirelessRedstone {
    var channel: Int
    
    @JvmInline value class NbtAdapter(val tag: CompoundTag) : IWirelessRedstone {
        override var channel: Int
            get() = (tag.get("channel") as NumericTag?)?.asInt ?: NO_CHANNEL
            set(value) {
                tag.putInt("channel", value)
            }
    }
    
    
    companion object {
        /**
         * Default implementation of [IWirelessRedstone]
         */
        fun make(channel: Int): IWirelessRedstone {
            return object : IWirelessRedstone {
                override var channel: Int = channel
            }
        }
        const val NO_CHANNEL = -1
    }
}

interface IWirelessRedstoneTransmitter : IWirelessRedstone {
    /**
     * Call when your block starts receiving redstone power
     */
    fun onStartReceivingSignal(level: ServerLevel) {
        level.dataStorage.dungeonBuilderData.state.redstoneHandler.powerChannel(this.channel, level)
    }
    
    fun onStopReceivingSignal(level: ServerLevel) {
        level.dataStorage.dungeonBuilderData.state.redstoneHandler.unpowerChannel(this.channel, level)
    }
}


