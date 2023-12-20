import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    id("com.android.library")
}

group = "com.github.mesayah"
version = "1.0-SNAPSHOT"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm {
        jvmToolchain(17)
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
                api(libs.kotlin.test)
                implementation(kotlin("test"))
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

android {
    lint {
        abortOnError = false
    }

    namespace = "tabletop.shared"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/resources")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packagingOptions {
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/versions/9/previous-compilation-data.bin")
    }
}

dependencies {
    add("kspCommonMainMetadata", "io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
    //add("kspJvm","io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
    //add("kspJvmTest","io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
}

kotlin.sourceSets.commonMain { kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") }

tasks.withType<KotlinCompile<*>>().all {
    if (name != "kspCommonMainKotlinMetadata") dependsOn("kspCommonMainKotlinMetadata")
}
