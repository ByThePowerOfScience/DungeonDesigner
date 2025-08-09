import btpos.gradle.architectury.ArchAttributes
import btpos.gradle.preprocessor.MultiplatformPreTransformer_Fabric
import btpos.gradle.preprocessor.getFabricTransformers
import dev.architectury.transformer.Transform

plugins {
	id("com.github.johnrengelman.shadow")
}

architectury {
	platformSetupLoomIde()
	fabric()
}

// the below is straight from the template
configurations {
	val common by configurations.creating {
		isCanBeResolved = true
		isCanBeConsumed = false
		
		// this part is the only custom bit
		attributes {
			attribute(ArchAttributes.SOURCES_TYPE, "main")
			attribute(ArchAttributes.PLATFORM, "fabric")
		}
	}
	
	
	//compileClasspath.get().extendsFrom(common)
	runtimeClasspath.get().extendsFrom(common)
	getByName("developmentFabric").extendsFrom(common) // This is a hardcoded requirement of the transformer toolchain. No, I don't know why there are two configurations for this.
	
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
	modImplementation("dev.architectury:architectury-fabric:${rootProject.properties["architectury_api_version"]}")
	modImplementation("net.fabricmc:fabric-language-kotlin:1.13.3+kotlin.2.1.21")
	
	// This was originally in the template as `common(project(path=":common", configuration="namedElements"))`,
	// but I needed my own transformers to exist in the dev runs, so I changed it to variant-aware.
	
	// compile against the live stuff
	compileOnly(project(path=":common", configuration="namedElements")) {
		isTransitive = false
	}
	// run with the dev-transformed stuff
	project(path=":common", configuration="transformMainForDev_Fabric").let {
		"developmentFabric"(it) { isTransitive = false }
		runtimeOnly(it) { isTransitive = false }
		"common"(it) { isTransitive = false }
	}
	
	// shadow the prod stuff
	"shadowBundle"(project(path= ":common", configuration= "transformProductionFabric"))
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
