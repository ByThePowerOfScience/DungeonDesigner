import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.spongepowered.asm.gradle.plugins.MixinExtension
import org.spongepowered.asm.gradle.plugins.struct.DynamicProperties
import java.text.SimpleDateFormat
import java.util.*

buildscript {
    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/")
    }
    dependencies {
        classpath("org.spongepowered:mixingradle:0.7.+")
    }
}

apply(plugin = "org.spongepowered.mixin")

plugins {
    idea
    `maven-publish`
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    kotlin("plugin.assignment") version "2.0.0"
}

group = "btpos.mcmods"
version = "1.20-0.1.0"

val mod_id: String by properties
val vendor = "ByThePowerOfScience"

val mc_version: String by properties
val forge_version: String by properties

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

println(
    "Java: ${System.getProperty("java.version")} JVM: ${System.getProperty("java.vm.version")}(${
        System.getProperty(
            "java.vendor"
        )
    }) Arch: ${System.getProperty("os.arch")}"
)

jarJar.enable()

minecraft {
    mappings("parchment", "2023.09.03-1.20.1")
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs.all {
        mods {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", mod_id)
            property("terminal.jline", "true")
            mods {
                create(mod_id) {
                    source(sourceSets.main.get())
                }
            }
        }
    }

    runs.run {
        create("client") {
            property("log4j.configurationFile", "log4j2.xml")
            jvmArg("-XX:+AllowEnhancedClassRedefinition")
            args("--username", "Player")
        }

        create("server") {}
        create("gameTestServer") {}
        create("data") {
            workingDirectory(project.file("run"))
            args(
                    "--mod",
                    mod_id,
                    "--all",
                    "--output",
                    file("src/generated/resources/"),
                    "--existing",
                    file("src/main/resources")
            )
        }
    }
}

sourceSets.main.configure { resources.srcDirs("src/generated/resources/") }

repositories {
    mavenCentral()
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
    
    maven(url="https://maven.blamejared.com") // For Bookshelf
    maven(url="https://maven.createmod.net") // For Catnip renderer
    maven(url="https://modmaven.dev/") // For Catnip's flywheel dependency
//    maven(url="https://jitpack.io")
    flatDir {
        dirs("libs")
    }
}

fun getProperty(name: String): String {
    return project.findProperty(name)?.toString() ?: System.getProperty(name)
}

dependencies {
    minecraft("net.minecraftforge:forge:$mc_version-$forge_version")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    implementation("thedarkcolour:kotlinforforge:4.11.0")
    
    minecraftLibrary(jarJar("com.github.bythepowerofscience:brigadierdsl:1.0.0")) {
        jarJar.ranged(this, "[1,)")
    }
//
//    minecraftLibrary(jarJar("org.jetbrains.kotlin:kotlin-stdlib:2.1.21")) {
//        jarJar.ranged(this, "[2,2.2)")
//    }
    
    implementation(fg.deobf("net.darkhax.bookshelf:Bookshelf-Forge-1.20.1:20.2.12"))
    
    implementation(jarJar(fg.deobf("net.createmod.catnip:Catnip-Forge-1.20.1:0.8.44"))) {
        jarJar.ranged(this, "[0.8,0.9)")
    }
    
    runtimeOnly(fg.deobf("dev.engine-room.flywheel:flywheel-forge-1.20.1:1.0+"))
    
    testImplementation(kotlin("test"))
}

val Project.mixin: MixinExtension
    get() = extensions.getByType()

mixin.run {
    add(sourceSets.main.get(), "dungeondesigner.mixins.refmap.json")
    config("dungeondesigner.mixins.json")
    val debug = this.debug as DynamicProperties
    debug.setProperty("verbose", true)
    debug.setProperty("export", true)
    setDebug(debug)
}

tasks.withType<Jar> {
    archiveBaseName.set(mod_id)
    manifest {
        attributes(
            mapOf(
                    "Specification-Title" to mod_id,
                    "Specification-Vendor" to vendor,
                    "Specification-Version" to "1",
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version.toString(),
                    "Implementation-Vendor" to vendor,
                    "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
            )
        )
    }
    finalizedBy("reobfJar")
}


tasks.processResources {
    val replaceProperties = mapOf(
            "minecraft_version" to mc_version,
            "minecraft_version_range" to project.properties["minecraft_version_range"],
            "forge_version" to forge_version,
            "forge_version_range" to project.properties["forge_version_range"],
            "mod_id" to mod_id,
            "mod_name" to project.properties["mod_name"],
            "mod_license" to project.properties["mod_license"],
            "mod_version" to version,
            "mod_authors" to project.properties["mod_authors"],
            "mod_description" to project.properties["mod_description"]
    )
    
    inputs.properties(replaceProperties)
    
    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(replaceProperties)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("file://${project.projectDir}/mcmodsrepo")
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.test {
    useJUnitPlatform()
}

idea {
    module {
        isDownloadJavadoc = true
    }
}

assignment {
    annotation("btpos.mcmods.devutil.misc.KotlinAssignmentOverloadTarget")
}