import korlibs.korge.gradle.KorgeGradlePlugin
import korlibs.korge.gradle.korge

apply<KorgeGradlePlugin>()

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



korge {
    id = "com.github.mesayah"

    targetJvm()

    serializationJson()

    jvmMainClassName = "tabletop.client.MainKt"
}

dependencies {
    //add("commonMainApi", project(":korge-dragonbones"))

    add("commonMainApi", project(":commonJvm"))

    add("commonMainApi", "app.softwork:kotlinx-uuid-core:0.0.21")

    add("commonMainApi", "io.github.oshai:kotlin-logging-jvm:5.1.0")

    add("commonMainApi", "org.slf4j:slf4j-api:2.0.9")

    add("commonMainApi", "ch.qos.logback:logback-classic:1.4.11")

    add("commonMainApi", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    add("commonMainApi", platform("io.arrow-kt:arrow-stack:1.2.0"))
    add("commonMainApi", "io.arrow-kt:arrow-core")
    add("commonMainApi", "io.arrow-kt:arrow-fx-coroutines")
    add("commonMainApi", "io.arrow-kt:suspendapp:0.4.0")

    add("commonMainApi", "io.ktor:ktor-client-core:2.3.4")
    add("commonMainApi", "io.ktor:ktor-client-cio:2.3.4")
    add("commonMainApi", "io.ktor:ktor-client-resources:2.3.4")

    add("commonTestApi", kotlin("test"))

    add("commonTestApi", "io.kotest:kotest-assertions-core:5.7.2")
    add("commonTestApi", "io.kotest:kotest-runner-junit5:5.7.2")

    add("commonTestApi", "io.kotest.extensions:kotest-assertions-arrow:1.4.0")
    add("commonTestApi", "io.kotest.extensions:kotest-assertions-arrow-fx-coroutines:1.4.0")

    add("commonTestApi", "io.mockk:mockk:1.13.8")

}

tasks.withType<Test> {
    useJUnitPlatform()
}