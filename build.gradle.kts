plugins {
    kotlin("jvm").apply(false) version "1.9.0"
    kotlin("plugin.serialization").apply(false) version "1.9.0"
}

subprojects {
    repositories {
        mavenCentral()
        maven { url = uri("https://s01.oss.sonatype.org") }
        mavenLocal()
        google()
        gradlePluginPortal()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    }
}