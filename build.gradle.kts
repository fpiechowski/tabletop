plugins {
    kotlin("multiplatform").apply(false) version "1.9.20"
    kotlin("plugin.serialization").apply(false) version "1.9.20"
    id("com.google.devtools.ksp").apply(false) version "1.9.20-1.0.14"
}

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

subprojects {
    repositories {
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        mavenCentral()
    }
}
