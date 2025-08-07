package btpos.unittest.dungeondesigner

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class PlatformTestRunner : ParameterResolver, Extension {
	override fun supportsParameter(p0: ParameterContext?, p1: ExtensionContext?): Boolean {
		throw IllegalStateException("buildSrc transformer not merged!!!")
	}
	
	override fun resolveParameter(p0: ParameterContext?, p1: ExtensionContext?): Any? {
		throw IllegalStateException("buildSrc transformer not merged!!!")
	}
}