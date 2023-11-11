plugins {
    kotlin("multiplatform").apply(false) version "1.7.20"
    kotlin("plugin.serialization").apply(false) version "1.7.20"
}

repositories {
    mavenCentral()
    google()
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
