import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinxSerialization: String by project
val kotlinVersion: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.github.mesayah"
version = "1.0-SNAPSHOT"

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += listOf("-Xskip-prerelease-check", "-Xcontext-receivers")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

kotlin {
    jvmToolchain(8)
}

repositories {
    mavenCentral()
    maven { url = uri("https://s01.oss.sonatype.org") }
    mavenLocal()
    google()
    gradlePluginPortal()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
}

dependencies {
    api("app.softwork:kotlinx-uuid-core:0.0.21")

    api("io.insert-koin:koin-core:3.5.0")

    api("io.github.oshai:kotlin-logging-jvm:5.1.0")

    api("org.slf4j:slf4j-api:2.0.9")

    api("ch.qos.logback:logback-classic:1.4.11")

    api("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.0")

    api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    api(platform("io.arrow-kt:arrow-stack:1.2.0"))
    api("io.arrow-kt:arrow-core")
    api("io.arrow-kt:arrow-fx-coroutines")
    api("io.arrow-kt:arrow-fx-stm")
    api("io.arrow-kt:suspendapp:0.4.0")

    api("io.ktor:ktor-client-core:2.3.4")
    api("io.ktor:ktor-client-cio:2.3.4")
    api("io.ktor:ktor-client-resources:2.3.4")

    api("one.microstream:microstream-storage-embedded:08.01.01-MS-GA")
    api("one.microstream:microstream-storage-embedded-configuration:08.01.01-MS-GA")

    testApi("io.insert-koin:koin-test:3.5.0")
    testApi("io.insert-koin:koin-test-junit4:3.5.0")
    testApi("io.insert-koin:koin-test-junit5:3.5.0")

    testApi("io.kotest:kotest-assertions-core:5.7.2")
    testApi("io.kotest:kotest-assertions-json:5.7.2")
    testApi("io.kotest:kotest-runner-junit5:5.7.2")

    testApi("io.kotest.extensions:kotest-assertions-arrow:1.4.0")
    testApi("io.kotest.extensions:kotest-extensions-koin:1.3.0")
    testApi("io.kotest.extensions:kotest-assertions-arrow-fx-coroutines:1.4.0")

    testApi("io.mockk:mockk:1.13.8")
}