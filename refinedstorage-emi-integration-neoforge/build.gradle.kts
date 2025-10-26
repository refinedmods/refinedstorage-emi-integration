plugins {
    id("com.refinedmods.refinedarchitect.neoforge")
}

repositories {
    maven {
        name = "Refined Storage"
        url = uri("https://maven.creeperhost.net")
        content {
            includeGroup("com.refinedmods.refinedstorage")
        }
    }
    maven {
        name = "EMI"
        url = uri("https://maven.terraformersmc.com/")
    }
}

refinedarchitect {
    modId = "refinedstorage_emi_integration"
    neoForge()
    publishing {
        maven = true
        curseForge = "1230691"
        curseForgeRequiredDependencies = listOf("refined-storage", "emi")
        modrinth = "TCSDwmbf"
        modrinthRequiredDependencies = listOf("refined-storage", "emi")
    }
}

base {
    archivesName.set("refinedstorage-emi-integration-neoforge")
}

val refinedstorageVersion: String by project
val refinedstorageQuartzArsenalVersion: String by project
val emiVersion: String by project

val commonJava by configurations.existing
val commonResources by configurations.existing

dependencies {
    compileOnly(project(":refinedstorage-emi-integration-common"))
    commonJava(project(path = ":refinedstorage-emi-integration-common", configuration = "commonJava"))
    commonResources(project(path = ":refinedstorage-emi-integration-common", configuration = "commonResources"))
    api("com.refinedmods.refinedstorage:refinedstorage-neoforge:${refinedstorageVersion}")
    runtimeOnly("dev.emi:emi-neoforge:${emiVersion}")
    compileOnlyApi("dev.emi:emi-neoforge:${emiVersion}")
    compileOnlyApi("com.refinedmods.refinedstorage:refinedstorage-quartz-arsenal-neoforge:${refinedstorageQuartzArsenalVersion}")
    // runtimeOnly("com.refinedmods.refinedstorage:refinedstorage-quartz-arsenal-neoforge:${refinedstorageQuartzArsenalVersion}")
}
