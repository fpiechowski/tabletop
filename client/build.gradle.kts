plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.palantir.docker") version "0.35.0"
}

group = "com.github.mesayah"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

val fritz2Version = "1.0-RC12"

kotlin {
    js(IR) {
        browser {
            webpackTask {
                devServer?.port = 8081
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common"))

                implementation("dev.fritz2:core:$fritz2Version")
                implementation("dev.fritz2:headless:$fritz2Version")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation("io.kotest:kotest-assertions-core:5.7.2")
                implementation("io.kotest:kotest-framework-engine:5.7.2")

                implementation("io.kotest.extensions:kotest-assertions-arrow:1.4.0")
                implementation("io.kotest.extensions:kotest-assertions-arrow-fx-coroutines:1.4.0")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm("tailwindcss", "3.3.5"))

                implementation(devNpm("postcss", "8.4.17"))
                implementation(devNpm("postcss-loader", "7.0.1"))
                implementation(devNpm("autoprefixer", "10.4.12"))
                implementation(devNpm("css-loader", "6.7.1"))
                implementation(devNpm("style-loader", "3.3.1"))
                implementation(devNpm("cssnano", "5.1.13"))

                implementation ("io.nacular.doodle:core:0.9.3"   )
                implementation ("io.nacular.doodle:browser:0.9.3") {
                    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
                    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-datetime")
                }
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

dependencies {
    add("kspCommonMainMetadata","io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
}