val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val kotlinxSerialization: String by project
val kotlinxDateTime: String by project

val mainClass = "tabletop.server.MainKt"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

project.setProperty("mainClassName", mainClass)

group = "com.github.mesayah"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("tabletop.server.MainKt")
}

tasks.withType<Test> {
    useJUnitPlatform()
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
    api(project(":commonJvm"))

    implementation("app.softwork:kotlinx-uuid-core:0.0.21")

    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

    implementation("org.slf4j:slf4j-api:2.0.9")

    implementation("ch.qos.logback:logback-classic:1.4.11")

    implementation("io.ktor:ktor-server-tomcat:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")

    implementation("one.microstream:microstream-storage-embedded:08.01.01-MS-GA")
    implementation("one.microstream:microstream-storage-embedded-configuration:08.01.01-MS-GA")

    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")
    testImplementation("io.ktor:ktor-client-resources:$ktorVersion")

    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")

    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")

    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.4.0")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow-fx-coroutines:1.4.0")
    testImplementation("io.kotest.extensions:kotest-extensions-koin:1.3.0")

    testImplementation("io.mockk:mockk:1.13.8")
}