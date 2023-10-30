rootProject.name = "tabletop"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    val kotlinVersion: String by settings
    val kotlinxBenchmark: String by settings

    plugins {
        kotlin("multiplatform") version kotlinVersion // TODO maybe delete
        kotlin("plugin.serialization") version kotlinVersion
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
    }
}

/*
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
*/


include("commonJvm", "server")
include("client")
