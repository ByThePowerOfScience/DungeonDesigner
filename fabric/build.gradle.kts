import btpos.gradle.preprocessor.MultiplatformPreTransformer_Fabric
import dev.architectury.transformer.Transform

plugins {
	id("com.github.johnrengelman.shadow")
}

architectury {
	platformSetupLoomIde()
	fabric()
}

configurations {
	val common by creating {
		isCanBeResolved = true
		isCanBeConsumed = false
	}
	compileClasspath.get().extendsFrom(common)
	runtimeClasspath.get().extendsFrom(common)
	getByName("developmentFabric").extendsFrom(common)
	
	testCompileClasspath.get().extendsFrom(common)
	testRuntimeClasspath.get().extendsFrom(common)
	// Files in this configuration will be bundled into your mod using the Shadow plugin.
	// Don"t use the `shadow` configuration from the plugin itself as it"s meant for excluding files.
	create("shadowBundle") {
		isCanBeResolved = true
		isCanBeConsumed = false
	}
}

dependencies {
	modImplementation ("net.fabricmc:fabric-loader:${rootProject.properties["fabric_loader_version"]}")
	
	modImplementation ("net.fabricmc.fabric-api:fabric-api:${rootProject.properties["fabric_api_version"]}")
	
	modImplementation ("dev.architectury:architectury-fabric:${rootProject.properties["architectury_api_version"]}")
	
	modImplementation("net.fabricmc:fabric-language-kotlin:1.13.3+kotlin.2.1.21")
	
	"common"(project(path= ":common", configuration= "transformProductionFabric")) { isTransitive = false }
	"shadowBundle" (project(path= ":common", configuration= "transformProductionFabric"))
}

tasks.processResources {
	inputs.property ("version", project.version)
	
	filesMatching("fabric.mod.json") {
		expand ("version" to project.version)
	}
}

tasks.shadowJar {
	configurations = listOf(project.configurations.getByName("shadowBundle"))
	archiveClassifier = "dev-shadow"
}

tasks.remapJar {
	input.set (tasks.shadowJar.get().archiveFile)
}
