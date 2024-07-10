pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://maven.pkg.github.com/refinedmods/refinedarchitect")
            credentials {
                username = "anything"
                password = "\u0067hp_oGjcDFCn8jeTzIj4Ke9pLoEVtpnZMP4VQgaX"
            }
        }
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "NeoForge"
            url = uri("https://maven.neoforged.net/releases")
        }
    }
    plugins {
        id("refinedarchitect.root").version("0.16.4")
        id("refinedarchitect.base").version("0.16.4")
        id("refinedarchitect.common").version("0.16.4")
        id("refinedarchitect.neoforge").version("0.16.4")
        id("refinedarchitect.fabric").version("0.16.4")
    }
}

rootProject.name = "refinedstorage-emi-integration"
include("refinedstorage-emi-integration-common")
include("refinedstorage-emi-integration-neoforge")
include("refinedstorage-emi-integration-fabric")
