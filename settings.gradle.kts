rootProject.name = "tabletop"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

include("common", "server", "client")
