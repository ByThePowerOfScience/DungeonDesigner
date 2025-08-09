package btpos.gradle.architectury

import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import org.gradle.kotlin.dsl.getByType

val Project.loom: net.fabricmc.loom.api.LoomGradleExtensionAPI
	get() = this.extensions.getByType()

object ArchAttributes {
	@JvmField
	val SOURCES_TYPE = Attribute.of("source_origin", String::class.java)
	
	@JvmField
	val PLATFORM = Attribute.of("arch_platform", String::class.java)
}