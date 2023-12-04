import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm()

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        commonMain.dependencies {
            implementation(project(":common"))

            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(libs.voyager.navigator)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.imageloader)
            implementation(libs.kotlinx.coroutines.core)

        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activityCompose)
            implementation(libs.compose.uitooling)
            implementation(libs.kotlinx.coroutines.android)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        jvmMain.dependencies {
            implementation(compose.desktop.common)
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    lint {
        baseline = file("lint-baseline.xml")
    }

    namespace = "tabletop.client"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        applicationId = "tabletop.client.AndroidApp"
        versionCode = 1
        versionName = "1.0.0"
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/resources")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packagingOptions {
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/versions/9/previous-compilation-data.bin")
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

tasks.withType<KotlinCompile<*>>().all {
    if (name != "kspCommonMainKotlinMetadata") dependsOn("kspCommonMainKotlinMetadata")
}
