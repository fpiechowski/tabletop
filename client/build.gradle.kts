import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.compose)
    alias(libs.plugins.ksp)

}

kotlin {
    jvm()

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        commonMain.dependencies {
            implementation(project(":shared"))

            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(libs.voyager.navigator)
            implementation("com.arkivanov.decompose:decompose:2.2.2")
            implementation("com.arkivanov.decompose:extensions-compose-jetbrains:2.2.2")
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.imageloader)
            implementation(libs.reaktive)
            implementation(libs.reaktive.coroutines)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        jvmMain.dependencies {
            implementation(compose.desktop.common)
            implementation(compose.desktop.currentOs)
            implementation(libs.jgit)
            implementation(libs.stablediffussion.sdk)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.github.mesayah.desktopApp"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", "io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
    //add("kspJvm","io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
    //add("kspJvmTest","io.arrow-kt:arrow-optics-ksp-plugin:1.2.1")
}

kotlin.sourceSets.commonMain { kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") }

