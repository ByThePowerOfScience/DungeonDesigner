
import btpos.gradle.architectury.loom
import btpos.gradle.preprocessor.MultiplatformPreTransformer_Fabric
import btpos.gradle.preprocessor.MultiplatformPreTransformer_Forge
import dev.architectury.plugin.TransformingTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("dev.architectury.loom") apply false
	id("architectury-plugin")
	id("com.github.johnrengelman.shadow") version "8.1.1" apply false
//	id("dungeondesigner-preprocessor") apply false
	kotlin("jvm") version "2.1.21"
}

fun Project.prop(name: String): String {
	return this.properties[name] as String
}

architectury {
	minecraft = project.prop("minecraft_version")
}

allprojects {
	group = rootProject.prop("maven_group")
	version = rootProject.prop("mod_version")
}

val generatedResources = project(":common").file("src/generated")

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "dev.architectury.loom")
	apply(plugin = "architectury-plugin")
	apply(plugin = "maven-publish")
	
//	if (project.name != "common")
//		apply(plugin = "dungeondesigner-preprocessor")
	
	base {
		// Set up a suffixed format for the mod jar names, e.g. `example-fabric`.
		archivesName = "${rootProject.prop("archives_name")}-${project.prop("name")}"
	}
	
	repositories {
		// Add repositories to retrieve artifacts from in here.
		// You should only use this when depending on other mods because
		// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
		// See https://docs.gradle.org/current/userguide/declaring_repositories.html
		// for more information about repositories.
		
		maven {
			name = "ParchmentMC"
			url = uri("https://maven.parchmentmc.org")
		}
	}
	
	sourceSets.forEach {
		it.resources.srcDir(generatedResources)
	}
	
	
	dependencies {
		"minecraft"("net.minecraft:minecraft:${rootProject.prop("minecraft_version")}")
		@Suppress("UnstableApiUsage")
		"mappings"(loom.layered {
			officialMojangMappings()
			parchment("org.parchmentmc.data:parchment-1.21.8:2025.07.20@zip")
		})
		
		implementation(rootProject.libs.kotlin.reflect)
		
		testImplementation(kotlin("test"))
		testImplementation("org.hamcrest:hamcrest:3.0")
	}
	
	java {
		// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
		// if it is present.
		// If you remove this line, sources will not be generated.
		withSourcesJar()
		
		sourceCompatibility = JavaVersion.VERSION_21
		targetCompatibility = JavaVersion.VERSION_21
	}
	
	tasks.withType<JavaCompile>().configureEach {
		options.release = 21
		options.encoding = "UTF-8"
	}
	
	tasks.withType<KotlinCompile> {
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_21)
			freeCompilerArgs.add("-Xcontext-receivers")
		}
	}
	
	if (project.name != "common"){
		val transformedCommonTest by configurations.creating {
			isCanBeConsumed = false
			isCanBeResolved = true
			attributes {
				attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("${project.name}-test"))
			}
		}
		
		val transformedCommonMain by configurations.creating {
			isCanBeConsumed = false
			isCanBeResolved = true
		}
		
		val loaderId = when (project.name) {
			"neoforge" -> "NeoForge"
			else -> project.name.capitalize()
		}
		
		dependencies {
			add("transformedCommonTest", project(":common"))
			add("transformedCommonMain", project(path=":common", configuration="transformProduction$loaderId"))
		}
		
		tasks.withType<Test> {
			useJUnitPlatform()
			val testJarTransformed = transformedCommonTest.resolve().first()
			testClassesDirs += zipTree(testJarTransformed)
			this@withType.classpath += transformedCommonMain
			this@withType.classpath += transformedCommonTest
		}
	}
	
	
	
	
	// Configure Maven publishing.
//	publishing {
//		publications {
//			"mavenJava"<MavenPublication> {
//				artifactId = base.archivesName.get()
//				from(components.java)
//			}
//		}
//
//		// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
//		repositories {
//			// Add repositories to publish to here.
//			// Notice=This block does NOT have the same function as the block in the top level.
//			// The repositories here will be used for publishing your artifact, not for
//			// retrieving dependencies.
//		}
//	}
}

//// Make the rootproject "test" task only run the tests in NeoForge
////      and not execute any tests in common where they're doomed to fail.
//tasks.replace("test", Test::class.java).configure<Task> {
//	dependsOn(project(":neoforge").tasks.test)
//}
//
//// Create a testAll task that runs the unit tests for every platform
//tasks.register<DefaultTask>("testAll").configure<DefaultTask> {
//	subprojects.filter { "common" !in it.name }
//		.map { it.tasks.test }
//		.forEach {
//			this@configure.dependsOn(it)
//		}
//}
