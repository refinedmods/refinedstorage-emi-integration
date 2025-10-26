plugins {
    id("com.refinedmods.refinedarchitect.fabric")
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
        name = "ModMenu"
        url = uri("https://maven.terraformersmc.com/")
    }
    maven {
        name = "Cloth Config"
        url = uri("https://maven.shedaniel.me/")
    }
    maven {
        name = "EMI"
        url = uri("https://maven.terraformersmc.com/")
    }
}

refinedarchitect {
    modId = "refinedstorage_emi_integration"
    fabric()
    publishing {
        maven = true
        curseForge = "1230691"
        curseForgeRequiredDependencies = listOf("fabric-api", "refined-storage", "emi")
        modrinth = "TCSDwmbf"
        modrinthRequiredDependencies = listOf("fabric-api", "refined-storage", "emi")
    }
}

base {
    archivesName.set("refinedstorage-emi-integration-fabric")
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
    modApi("com.refinedmods.refinedstorage:refinedstorage-fabric:${refinedstorageVersion}")
    modRuntimeOnly("dev.emi:emi-fabric:${emiVersion}")
    modCompileOnlyApi("dev.emi:emi-fabric:${emiVersion}")
    modCompileOnlyApi("com.refinedmods.refinedstorage:refinedstorage-quartz-arsenal-fabric:${refinedstorageQuartzArsenalVersion}")
    // modRuntimeOnly("com.refinedmods.refinedstorage:refinedstorage-quartz-arsenal-fabric:${refinedstorageQuartzArsenalVersion}")
}
