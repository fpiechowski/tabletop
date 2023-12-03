import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
}

group = "com.github.mesayah"
version = "1.0-SNAPSHOT"

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
                api(libs.kotlin.logging)

                api(libs.kotlinx.uuid)

                api(libs.kotlinx.serialization.json)

                api(libs.kotlinx.datetime)

                api(libs.arrow.core)
                api(libs.arrow.core.serialization)
                api(libs.arrow.fx.coroutines)
                api(libs.arrow.optics)
                api(libs.suspendapp)

                api(libs.ktor.client.core)
            }
        }

        val jvmMain by getting {
            dependencies {
                api(libs.logback)

                //api(libs.kotlin.logging.jvm)

                api(libs.ktor.client.cio)
            }
        }

        val commonTest by getting {
            dependencies {
            }
        }

        val jvmTest by getting {
            dependencies {
                api(libs.kotest.assertions.core)
                api(libs.kotest.assertions.json)
                api(libs.kotest.runner.junit5)

                api(libs.kotest.assertions.arrow)
                api(libs.kotest.assertions.arrow.fx.coroutines)

                api(libs.mockk)
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", "io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
    add("kspJvm","io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
    //add("kspJvmTest","io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
}

kotlin.sourceSets.commonMain { kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") }

//tasks.withType<KotlinCompile<*>>().all {
//    if (name != "kspCommonMainKotlinMetadata") dependsOn("kspCommonMainKotlinMetadata")
//}
