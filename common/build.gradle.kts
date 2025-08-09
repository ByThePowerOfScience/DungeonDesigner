import btpos.gradle.architectury.ArchAttributes
import btpos.gradle.preprocessor.getForgeTransformers
import btpos.gradle.preprocessor.getFabricTransformers
import dev.architectury.plugin.TransformingTask
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
		
		for (loader in settings.loaders) {
			// register our "transform for dev" task
			val platform = loader.titledId
			makeTransformingTask(platform, "transformMainForDev_$platform", tasks.jar.get(), "main")
			makeTransformingTask(platform, "transformTestForDev_$platform", testJar.get(), "test")
		}
	}
}

fun makeTransformingTask(platform: String, configName: String, jarTask: Jar, sourceSet: String) {
	val id = platform.lowercase()
	
	project.configurations.maybeCreate(configName).apply {
		isCanBeConsumed = true
		isCanBeResolved = false
		attributes {
			attribute(ArchAttributes.SOURCES_TYPE, sourceSet)
			attribute(ArchAttributes.PLATFORM, id)
		}
	}
	
	// Register a transformingtask with no transformers,
	//  because the below tasks.withType thing will add it to ALL transforming tasks including this!
	val transformerTask = project.tasks.register<TransformingTask>("jar_$configName") {
		dependsOn(jarTask)
		
		input = jarTask.archiveFile
		
		this.platform = id
		
		archiveClassifier = configName
	}
	
	transformerTask.get().archiveFile.get().asFile.takeIf { !it.exists() }?.createEmptyJar()
	
	project.artifacts.add(configName, transformerTask)
}

// Add my transformers to the prod variants
project.afterEvaluate {
	tasks.withType<TransformingTask> {
		val id = platform?.lowercase() ?: return@withType
		val customTransformers = when (id) {
			"neoforge" -> getForgeTransformers()
			"fabric" -> getFabricTransformers()
			else -> throw IllegalStateException("Platform \"$platform\" not specified! If it doesn't have transformers, it needs an empty list!")
		}
		
		inputs.file(this.input)
		
		inputs.property("transformers", customTransformers.joinToString(",") { it.javaClass.toString() })
		
		customTransformers.forEach {
			add(it, { _, _ -> })
		}
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