plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
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

kotlin {
    val targetAttribute = Attribute.of("target", String::class.java)

    jvm {
        jvmToolchain(11)
        attributes.attribute(targetAttribute, "client")
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
        compilations.all {
            kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"
        }
        mainRun {
            mainClass.set("tabletop.client.MainKt")
        }
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":commonJvm"))

                implementation("app.softwork:kotlinx-uuid-core:0.0.21")

                implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

                implementation("org.slf4j:slf4j-api:2.0.9")

                implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

                implementation("ch.qos.logback:logback-classic:1.4.11")

                val gdxVersion: String by project
                implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
                implementation("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop")
                implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
                implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")

                val ktxVersion: String by project
                api("com.badlogicgames.gdx:gdx:$gdxVersion")
                api("io.github.libktx:ktx-app:$ktxVersion")
                api("io.github.libktx:ktx-assets:$ktxVersion")
                api("io.github.libktx:ktx-async:$ktxVersion")
                api("io.github.libktx:ktx-graphics:$ktxVersion")
                api("io.github.libktx:ktx-actors:$ktxVersion")
                api("io.github.libktx:ktx-freetype:$ktxVersion")
                api("io.github.libktx:ktx-scene2d:$ktxVersion")
                api("io.github.libktx:ktx-vis:$ktxVersion")
                api("io.github.libktx:ktx-vis-style:$ktxVersion")
            }
        }

        val jvmTest by getting {
        }

        val commonMain by getting {
            dependencies {
                implementation("app.softwork:kotlinx-uuid-core:0.0.21")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

                implementation(platform("io.arrow-kt:arrow-stack:1.2.0"))
                implementation("io.arrow-kt:arrow-core")
                implementation("io.arrow-kt:arrow-fx-coroutines")
                implementation("io.arrow-kt:suspendapp:0.4.0")

                implementation("io.ktor:ktor-client-core:2.3.4")
                implementation("io.ktor:ktor-client-cio:2.3.4")
                implementation("io.ktor:ktor-client-resources:2.3.4")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))

                implementation("io.kotest:kotest-assertions-core:5.7.2")
                implementation("io.kotest:kotest-runner-junit5:5.7.2")

                implementation("io.kotest.extensions:kotest-assertions-arrow:1.4.0")
                implementation("io.kotest.extensions:kotest-assertions-arrow-fx-coroutines:1.4.0")

                implementation("io.mockk:mockk:1.13.8")
            }
        }
    }
}
