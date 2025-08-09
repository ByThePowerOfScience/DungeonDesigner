import btpos.gradle.architectury.ArchAttributes

plugins {
	id("com.github.johnrengelman.shadow")
}

architectury {
	platformSetupLoomIde()
	neoForge()
}

configurations {
	val common by creating {
		isCanBeResolved = true
		isCanBeConsumed = false
		attributes {
			attribute(ArchAttributes.SOURCES_TYPE, "main")
			attribute(ArchAttributes.PLATFORM, "neoforge")
		}
	}
	compileClasspath.get().extendsFrom(common)
	runtimeClasspath.get().extendsFrom(common)
	getByName("developmentNeoForge").extendsFrom(common)
	
	// Files in this configuration will be bundled into your mod using the Shadow plugin.
	// Don"t use the `shadow` configuration from the plugin itself as it"s meant for excluding files.
	create("shadowBundle") {
		isCanBeResolved = true
		isCanBeConsumed = false
	}
}

repositories {
	maven {
		name = "NeoForged"
		url = uri("https://maven.neoforged.net/releases")
	}
	maven {
		name = "Kotlin for Forge"
		url = uri("https://thedarkcolour.github.io/KotlinForForge/")
	}
}

dependencies {
	neoForge("net.neoforged:neoforge:${rootProject.properties["neoforge_version"]}")
	
	modImplementation("dev.architectury:architectury-neoforge:${rootProject.properties["architectury_api_version"]}")
	
	implementation("thedarkcolour:kotlinforforge-neoforge:5.9.0")
	
	testImplementation("net.neoforged:testframework:${rootProject.properties["neoforge_version"]}")
	
	"common"(project(":common")) { isTransitive = false }
	"shadowBundle"(project(path = ":common", configuration = "transformProductionNeoForge"))
}

tasks.processResources {
	inputs.property("version", project.version)
	
	filesMatching("META-INF/neoforge.mods.toml") {
		expand("version" to project.version)
	}
}

loom {
	runs {
		create("clientData") {
			@Suppress("UnstableApiUsage")
			clientData()
			programArgs("--all", "--mod", "dungeondesigner")
			programArgs("--output", project.rootProject.file("src/generated").absolutePath)
			programArgs("--existing", project.rootProject.file("src/main/resources").absolutePath)
		}
	}
}

tasks.shadowJar {
	configurations = listOf(project.configurations.getByName("shadowBundle"))
	archiveClassifier = "dev-shadow"
}

tasks.remapJar {
	input.set(tasks.shadowJar.get().archiveFile)
}