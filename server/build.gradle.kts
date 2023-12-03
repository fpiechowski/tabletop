import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

val ktorVersion: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

application {
    mainClass = "tabletop.server.MainKt"
}

tasks.named<Jar>("shadowJar") {
    this.archiveFileName.set("tabletop-server-$version.jar")
}

group = "com.github.mesayah"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.server.websockets)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.microstream.storage.embedded)
                implementation(libs.microstream.storage.embedded.configuration)
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata",libs.arrow.optics.ksp.plugin)
    add("kspJvm",libs.arrow.optics.ksp.plugin)
    add("kspJvmTest",libs.arrow.optics.ksp.plugin)
}

kotlin.sourceSets.commonMain { kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") }

