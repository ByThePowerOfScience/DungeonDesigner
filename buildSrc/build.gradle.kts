plugins {
	`kotlin-dsl`
}

repositories {
	gradlePluginPortal()
	mavenCentral()
	maven(url="https://maven.fabricmc.net/")
	maven(url="https://maven.architectury.dev/")
	maven(url="https://files.minecraftforge.net/maven/")
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:2.1.21")
	implementation("org.ow2.asm:asm:9.4")
	implementation("org.ow2.asm:asm-commons:9.4")
	implementation("org.ow2.asm:asm-tree:9.4")
}

gradlePlugin {
	plugins {
		create("preprocessor") {
			id = "dungeondesigner-preprocessor"
			implementationClass = "btpos.gradle.preprocessor.PreprocessorPlugin"
		}
	}
}