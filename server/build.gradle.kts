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
        jvmToolchain(11)
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
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-websockets:$ktorVersion")
                implementation("io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("one.microstream:microstream-storage-embedded:08.01.01-MS-GA")
                implementation("one.microstream:microstream-storage-embedded-configuration:08.01.01-MS-GA")
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata","io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
    add("kspJvm","io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
    add("kspJvmTest","io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
}
