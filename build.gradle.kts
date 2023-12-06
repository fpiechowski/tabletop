plugins {
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlinx.serialization).apply(false)
}

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    google()
}

subprojects {
    repositories {
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        mavenCentral()
        google()
    }
}
