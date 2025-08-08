import btpos.gradle.preprocessor.MultiplatformPreTransformer_Fabric
import btpos.gradle.preprocessor.MultiplatformPreTransformer_Forge
import dev.architectury.plugin.ModLoader.Companion.applyNeoForgeForgeLikeProd
import dev.architectury.plugin.TransformingTask
import dev.architectury.plugin.loom.LoomInterface
import org.gradle.kotlin.dsl.withType
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

fun File.createEmptyJar() {
	parentFile.mkdirs()
	JarOutputStream(outputStream(), Manifest()).close()
}

val testJar = tasks.register("testJar", Jar::class) {
	group = "build"
	dependsOn(tasks.testClasses)
	// testClasses doesn't have outputs for some reason??? so we do it manually
	from(project.layout.buildDirectory.file("classes/kotlin/test/"), project.layout.buildDirectory.file("classes/java/test/"))
	archiveClassifier = "testJar"
}

architectury {
	common((rootProject.properties["enabled_platforms"] as String).split(",")) {
		// This whole block is so I can get :common's test classes ONTO the target platform WITH transformations applied
		//      so I can have platform-specific testrunners for tests defined in :common,
		//      since :common can't run unit tests without crashing.
		// It's also almost entirely copied one-to-one from ArchitecturyPluginExtension#common
		
		val settings = this
		val loom = LoomInterface.get(project)
		
		
		for (loader in settings.loaders) {
			project.configurations.maybeCreate("transformProduction${loader.titledId}Test")
			// CUSTOM: Define variant with name so I can resolve it in the platform-specific test task
			project.configurations.getByName("transformProduction${loader.titledId}Test") {
				isCanBeConsumed = true
				isCanBeResolved = false
				attributes {
					attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("${loader.id}-test"))
				}
			}
			val transformProductionTask =
				project.tasks.register("transformProduction${loader.titledId}Test", TransformingTask::class.java) {
					val it = this@register
					it.group = "Architectury"
					it.platform = loader.id
					loader.transformProduction(it, loom, settings)
					
					if (settings.isForgeLike && loader.id == "neoforge") {
						it.addPost(applyNeoForgeForgeLikeProd(loom, settings))
					}
					
					it.archiveClassifier.set("transformProduction${loader.titledId}Test")
					it.input.set(testJar.get().archiveFile)
					
					it.dependsOn(testJar)
//					buildTask.dependsOn(it)
				}
			project.artifacts.add("transformProduction${loader.titledId}Test", transformProductionTask)
			
			transformProductionTask.get().archiveFile.get().asFile.takeUnless { it.exists() }?.createEmptyJar()
		}
	}
}

tasks.withType<TransformingTask> {
	if (platform?.contains("forge") == true) {
		add(MultiplatformPreTransformer_Forge()) {_, _ ->}
	} else if (platform == "fabric") {
		add(MultiplatformPreTransformer_Fabric()) { _, _ ->}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

dependencies {
	// We depend on Fabric Loader here to use the Fabric @Environment annotations,
	// which get remapped to the correct annotations on each platform.
	// Do NOT use other classes from Fabric Loader.
	modImplementation("net.fabricmc:fabric-loader:${rootProject.properties["fabric_loader_version"]}")
	
	modImplementation("dev.architectury:architectury:${rootProject.properties["architectury_api_version"]}")
}

/**
 * Some tasks are created after the project has been evaluated, and so will failhard if we reference them directly.
 */
fun <T : Task> getFutureTask(name: String): T {
	return tasks.matching { it.name == name }.first() as T
}

//getFutureTask<TransformingTask>("transformProductionNeoForge").configure<TransformingTask> {
//	archiveClassifier.set("transformNeoForge")
//}
//getFutureTask<TransformingTask>("transformProductionFabric").configure<TransformingTask> {
//	archiveClassifier.set("transformFabric")
//}

//configurations {
//	val transformedNeoForge by creating {
//		isCanBeConsumed = true
//		isCanBeResolved = false
//		attributes {
//			attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
//			attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
//			attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("neoforge-transformed"))
//		}
//	}
//	val transformedFabric by creating {
//		isCanBeConsumed = true
//		isCanBeResolved = false
//		attributes {
//			attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
//			attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
//			attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("fabric-transformed"))
//		}
//	}
//	create("transformedNeoForgeTest") {
//		extendsFrom(transformedNeoForge)
//		attributes {
//			attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("neoforge-transformed-test"))
//		}
//	}
//	create("transformedFabricTest") {
//		extendsFrom(transformedFabric)
//		attributes {
//			attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("fabric-transformed-test"))
//		}
//	}
//}
//
//artifacts {
//	add("transformedNeoForge", getFutureTask("transformProductionNeoForge"))
//	add("transformedFabric", getFutureTask("transformProductionFabric"))
//}





tasks.test {
	exclude("**/*")
}