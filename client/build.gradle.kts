plugins {
    kotlin("multiplatform")
    id("io.kotest.multiplatform") version "5.8.0"
    id("com.palantir.docker") version "0.35.0"
    id("com.google.devtools.ksp") version "1.7.20-1.0.6"
}

group = "com.github.mesayah"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://s01.oss.sonatype.org") }
    mavenLocal()
    google()
    gradlePluginPortal()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
}

val fritz2Version = "1.0-RC12"

kotlin {
    js(IR) {
        browser()
    }.binaries.executable()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common"))

                implementation("dev.fritz2:core:$fritz2Version")

                implementation("app.softwork:kotlinx-uuid-core:0.0.22")

                implementation("org.slf4j:slf4j-api:2.0.9")

                implementation("ch.qos.logback:logback-classic:1.4.11")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

                implementation("io.arrow-kt:arrow-core:1.2.0")
                implementation("io.arrow-kt:arrow-fx-coroutines:1.2.0")
                implementation("io.arrow-kt:suspendapp:0.4.0")

                implementation("io.ktor:ktor-client-core:2.3.6")
                implementation("io.ktor:ktor-client-js:2.3.6")
                implementation("io.ktor:ktor-client-resources:2.3.6")


            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))

                implementation("io.kotest:kotest-assertions-core:5.7.2")
                implementation("io.kotest:kotest-framework-engine:5.7.2")

                implementation("io.kotest.extensions:kotest-assertions-arrow:1.4.0")
                implementation("io.kotest.extensions:kotest-assertions-arrow-fx-coroutines:1.4.0")
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", "dev.fritz2:lenses-annotation-processor:$fritz2Version")
}

kotlin.sourceSets.commonMain { kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") }

tasks.withType<Test> {
    dependsOn(":server:buildDockerImage")

    useJUnitPlatform()
}

