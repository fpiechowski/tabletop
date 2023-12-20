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
                implementation(project(":shared"))
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
    //add("kspCommonMainMetadata", "io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
    add("kspJvm", "io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
}

kotlin.sourceSets.commonMain { kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") }

//tasks.withType<KotlinCompile<*>>().all {
//    if (name != "kspCommonMainKotlinMetadata") dependsOn("kspCommonMainKotlinMetadata")
//}
