import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.github.mesayah"
version = "1.0-SNAPSHOT"

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"
}

kotlin {
    jvmToolchain(11)
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

    testApi("io.kotest:kotest-assertions-core:5.7.2")
    testApi("io.kotest:kotest-assertions-json:5.7.2")
    testApi("io.kotest:kotest-runner-junit5:5.7.2")

    testApi("io.kotest.extensions:kotest-assertions-arrow:1.4.0")
    testApi("io.kotest.extensions:kotest-assertions-arrow-fx-coroutines:1.4.0")

    testApi("io.mockk:mockk:1.13.8")
}