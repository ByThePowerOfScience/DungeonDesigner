package btpos.unittest

import btpos.mcmods.dungeondesigner.CommonEntry
import net.minecraft.SharedConstants
import net.minecraft.server.Bootstrap
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.util.concurrent.atomic.AtomicBoolean

/**
 * See fabric's btpos.unittest.EphemeralTestServerProvider and Neo's of the same
 */
class PlatformTestRunner : BeforeAllCallback, Extension {
	val hasMCBeenInitialized = AtomicBoolean(false)
	
	override fun beforeAll(p0: ExtensionContext?) {
		if (hasMCBeenInitialized.compareAndSet(false, true)) {
			SharedConstants.tryDetectVersion()
			Bootstrap.bootStrap()
			CommonEntry.init()
		}
	}
}