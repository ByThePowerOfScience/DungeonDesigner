package btpos.mcmods.dungeondesigner.common.redstone

import btpos.mcmods.devutil.common.ext.vanilla.world.modifyBlockAndUpdate
import btpos.mcmods.devutil.common.ext.vanilla.world.with
import btpos.mcmods.devutil.common.util.serialization.ICodecSerializable
import btpos.mcmods.devutil.common.structure.composition.IOnChange
import btpos.mcmods.dungeondesigner.MOD_LOGGER
import btpos.mcmods.dungeondesigner.POWERED
import btpos.mcmods.dungeondesigner.builder.redstone.IWirelessRedstone.Companion.NO_CHANNEL
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import kotlin.collections.forEach

/**
 * Negotiates between the wireless redstone transmitters and receivers.  Updates receivers when transmitters power or unpower.
 *
 * Main reason this exists at all is because if we have multiple transmitters on a single channel,
 * we need to make sure unpowering one transmitter doesn't make every receiver unpower while there's another transmitter powering the same channel.
 */
class WirelessRedstoneController(
	/**
	 * Channels are stored by index.
	 * @see btpos.mcmods.dungeondesigner.builder.redstone.IWirelessRedstone.channel
	 */
    pChannels: ArrayList<WirelessRedstoneChannel> = ArrayList()
) : ICodecSerializable<WirelessRedstoneController>, IOnChange {
    //region ICodecSerializable
    override fun codec() = CODEC
	override fun copyFrom(other: WirelessRedstoneController) {
		this.channels = other.channels
	}
    //endregion
	companion object {
		val CODEC = RecordCodecBuilder.create {
			it.group(
				Codec.list(WirelessRedstoneChannel.CODEC).fieldOf("channels").forGetter(WirelessRedstoneController::channels),
			).apply(it) { WirelessRedstoneController(ArrayList(it)) }
        }
	}
	
	var channels = pChannels
		private set
	
	override var onChange: () -> Unit = {}
	
	/**
	 * @return Index of newly created channel.
	 */
	fun makeNewChannel(): Int {
		channels.add(WirelessRedstoneChannel())
		onChange()
		return channels.size - 1
	}
	
	fun isChannelPowered(channel: Int): Boolean {
		return channels.getOrNull(channel)?.numTransmitting?.let { it > 0 } ?: false
	}
	
	/**
	 * Called by transmitters when powered. Only causes updates if the channel isn't already powered by someone else.
	 *
	 * If this method call changes the channel from unpowered to powered, this method manually changes all of the channel's target blocks to be POWERED=true.
	 */
	fun powerChannel(channel: Int, level: ServerLevel) {
		val channelObj = safeGetChannel(channel)
		
		val numPreviouslyPowering = channelObj.numTransmitting++
		onChange()
		
		if (numPreviouslyPowering > 0)
		// Someone was already powering this before us, so we haven't changed anything.
			return
		
		// We're the first one powering this, we gotta notify all listeners
		channelObj.receivers.forEach { level.modifyBlockAndUpdate(it) { it.with(POWERED, true) } }
	}
	
	/**
	 * Called by a transmitter to power the channel. Only causes updates if it's the only one powering the channel.
	 *
	 * If this method call changes the channel from unpowered to powered, this method manually changes all of the channel's target blocks to be POWERED=true.
	 */
	fun unpowerChannel(channel: Int, level: ServerLevel) {
		val channelObj = safeGetChannel(channel)
		
		val currentlyPowering = --channelObj.numTransmitting
		onChange()
		
		if (currentlyPowering > 0)
			return // No change
		
		channelObj.receivers.forEach { level.modifyBlockAndUpdate(it) { if (it.hasProperty(POWERED)) it.with(POWERED, false) else it } }
	}
	
	/**
	 * Registers this position as a listener to a given channel.  Makes the channel if [NO_CHANNEL] is given for [pChannel].
	 * @return the channel this block is listening to.
	 */
	fun registerWirelessReceiver(pChannel: Int, pos: BlockPos): Int {
		val channel = when (pChannel) {
			NO_CHANNEL -> makeNewChannel()
			else -> pChannel
		}
		safeGetChannel(pChannel).receivers.add(pos)
		onChange()
		return channel
	}
	
	fun unregisterWirelessReceiver(channel: Int, pos: BlockPos) {
		channels.getOrNull(channel)?.receivers?.remove(pos) ?: return MOD_LOGGER.error("Error when removing wireless receiver: No channel \"{}\" found!", channel)
		onChange()
	}
	
	/**
	 * NOTE: There should never be a case where the channel doesn't exist, because the moment a block is placed down without a channel, it's assigned to a new channel.
	 */
	private fun safeGetChannel(channel: Int): WirelessRedstoneChannel {
		require(channel != NO_CHANNEL) { "Error in wireless redstone: attempted to get the NO_CHANNEL channel!" }
		require(channel in channels.indices) { "Error in wireless redstone: attempted to power channel $channel when it doesn't exist!!! This shouldn't happen!" }
		return channels[channel]
	}
}

data class WirelessRedstoneChannel(
    /**
	 * Locations of every receiver listening to this channel. Used to set their state to "POWERED" or "UNPOWERED".
	 */
	val receivers: MutableSet<BlockPos>,
    /**
	 * Count of how many transmitters on this channel are currently powering it.
	 *
	 * Used when a transmitter powers or unpowers to know if the listeners should be updated accordingly, or if the transmitter is just adding its name to the hat.
	 */
	var numTransmitting: Int
) {
	constructor() : this(ObjectArraySet(), 0)
	
	companion object {
		val CODEC = RecordCodecBuilder.create {
			it.group(
				Codec.list(BlockPos.CODEC).fieldOf("receivers").forGetter { it.receivers.toList() },
				Codec.INT.fieldOf("num_transmitting").forGetter(WirelessRedstoneChannel::numTransmitting)
			).apply(it) { receivers, num_transmitting ->
				WirelessRedstoneChannel(ObjectArraySet(receivers), num_transmitting)
			}
		}
	}
}