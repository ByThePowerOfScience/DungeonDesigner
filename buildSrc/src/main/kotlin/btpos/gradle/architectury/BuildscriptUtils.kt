package btpos.gradle.architectury

import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

val Project.loom: net.fabricmc.loom.api.LoomGradleExtensionAPI
	get() = this.extensions.getByType()