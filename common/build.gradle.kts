import btpos.gradle.architectury.ArchAttributes
import btpos.gradle.preprocessor.getForgeTransformers
import btpos.gradle.preprocessor.getFabricTransformers
import dev.architectury.plugin.ArchitectPluginExtension
import dev.architectury.plugin.TransformingTask
import org.gradle.initialization.Environment
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
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
	/**
	 * The standard `common` method registers the tasks `transformProductionFabric` and `transformProductionNeoForge`,
	 * which have transformers made for the obfuscated prod environment.  These will break the deobf dev runs.
	 *
	 * Problem is, I still need to run transformers on my common module before merging it into the platform-specific ones.
	 *
	 * Since this is the only place we can actually learn what loaders are being targeted,
	 * we have to do all of our transformer task initialization here...
	 */
	common((rootProject.properties["enabled_platforms"] as String).split(",")) {
		val settings: ArchitectPluginExtension.CommonSettings = this
		makeTransformingTasks(settings)
	}
}



fun makeTransformingTasks(settings: ArchitectPluginExtension.CommonSettings) {
	for (loader in settings.loaders) {
		// register our "transform for dev" task
		val platform = loader.titledId
		//
		makeTransformingTask(platform, "transformMainForDev_$platform", tasks.jar.get(), "main")
		makeTransformingTask(platform, "transformTestForDev_$platform", testJar.get(), "test")
	}
}

/**
 * Make tasks and configurations that apply ONLY our transformers to the given source set.
 */
fun makeTransformingTask(platform: String, configName: String, jarTask: Jar, sourceSet: String) {
	val id = platform.lowercase()
	
	project.configurations.maybeCreate(configName).apply {
		isCanBeConsumed = true
		isCanBeResolved = false
		attributes { // TODO figure out what I need to do to make :fabric:compileClasspath take the devJar and not the runtimeElements jar
			attribute(ArchAttributes.SOURCES_TYPE, sourceSet)
			attribute(ArchAttributes.PLATFORM, id)
		}
	}
	
	// Registered with no transformers because we add ours to ALL transformtasks, including this one
	val transformerTask = project.tasks.register<TransformingTask>("jar_$configName") {
		dependsOn(jarTask)
		
		input = jarTask.archiveFile
		
		this.platform = id
		
		archiveClassifier = configName
	}
	
	transformerTask.get().archiveFile.get().asFile.takeIf { !it.exists() }?.createEmptyJar() // fix a filenotfound crash
	
	project.artifacts.add(configName, transformerTask) // add exported variant for this config
}

// Add my transformers to the prod variants as well as our dev variants
project.afterEvaluate {
	tasks.withType<TransformingTask> {
		val id = platform?.lowercase() ?: return@withType
		val customTransformers = when (id) {
			"neoforge" -> getForgeTransformers()
			"fabric" -> getFabricTransformers()
			else -> throw IllegalStateException("Platform \"$platform\" not specified! If it doesn't have transformers, it needs an empty list!")
		}
		
		// update when the sources are changed and if the list of transformers in buildSrc changes
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
	modImplementation("net.fabricmc:fabric-loader:${rootProject.properties["fabric_loader_version"]}")
	
	modImplementation("dev.architectury:architectury:${rootProject.properties["architectury_api_version"]}")
}

tasks.test {
	exclude("**/*")
}