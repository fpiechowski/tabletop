val kotlinxSerialization: String by project
val kotlinVersion: String by project

plugins {
    application
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "com.github.mesayah"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
kotlin {
    jvm()
    js(IR) {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.github.oshai:kotlin-logging:5.1.0")

                api("app.softwork:kotlinx-uuid-core:0.0.22")

                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

                api("io.arrow-kt:arrow-core:1.2.0")
                api("io.arrow-kt:arrow-fx-coroutines:1.2.0")
                api("io.arrow-kt:suspendapp:0.4.0")

                api("io.ktor:ktor-client-core:2.3.6")
                api("io.ktor:ktor-client-resources:2.3.6")
            }
        }

        val jvmMain by getting {
            dependencies {
                api("de.ruedigermoeller:fst:2.56")
                api("ch.qos.logback:logback-classic:1.4.11")

                api("io.ktor:ktor-client-cio:2.3.6")

                api("one.microstream:microstream-storage-embedded:08.01.01-MS-GA")
                api("one.microstream:microstream-storage-embedded-configuration:08.01.01-MS-GA")
            }
        }

        val commonTest by getting {
            dependencies {
                api(kotlin("test"))
            }
        }

        val jsMain by getting {
            dependencies {
                api("io.ktor:ktor-client-js:2.3.6")
            }
        }

        val jvmTest by getting {
            dependencies {
                api("io.kotest:kotest-assertions-core:5.7.2")
                api("io.kotest:kotest-assertions-json:5.7.2")
                api("io.kotest:kotest-runner-junit5:5.7.2")

                api("io.kotest.extensions:kotest-extensions-koin:1.3.0")
                api("io.kotest.extensions:kotest-assertions-arrow:1.4.0")
                api("io.kotest.extensions:kotest-assertions-arrow-fx-coroutines:1.4.0")

                api("io.mockk:mockk:1.13.8")
            }
        }
    }
}