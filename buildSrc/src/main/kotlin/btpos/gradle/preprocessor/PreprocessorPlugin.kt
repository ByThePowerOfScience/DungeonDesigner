package btpos.gradle.preprocessor

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer

//class PreprocessorPlugin : Plugin<Project> {
//	override fun apply(target: Project) {
//		val sourceSets = target.extensions.getByType(JavaPluginExtension::class.java).sourceSets
//		val platform = target.name // target.extensions.getByType(ArchitectPluginExtension::class.java).properties.get("architectury.platform.name")
//		target.plugins.withId("org.jetbrains.kotlin.jvm") { configurePlugin(platform, sourceSets, target, "kotlin") }
//	}
//
//	private fun configurePlugin(platform: String, sourceSets: SourceSetContainer, project: Project, language: String) {
//		sourceSets.forEach { sourceSet ->
//			project.tasks.named(sourceSet.getCompileTaskName(language)) {
//				val action = project.objects.newInstance(MultiplatformPreTransformer::class.java, platform)
//				doLast("fairyCompile", action)
//			}
//		}
//	}
//}