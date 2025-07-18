pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.parchmentmc.org")
        
    }
}

dependencyResolutionManagement {

}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}