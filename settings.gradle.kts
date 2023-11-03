rootProject.name = "tabletop"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    val kotlinVersion: String by settings

    plugins {
        kotlin("multiplatform") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
}

plugins {
    id("com.soywiz.kproject.settings") version "0.3.1" // Substitute by the latest version
}

include("commonJvm", "server")
include("client")

kproject("./deps")