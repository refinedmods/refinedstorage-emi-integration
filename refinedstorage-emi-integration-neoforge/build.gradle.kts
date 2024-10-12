plugins {
    id("refinedarchitect.neoforge")
}

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/refinedmods/refinedstorage2")
        credentials {
            username = "anything"
            password = "\u0067hp_oGjcDFCn8jeTzIj4Ke9pLoEVtpnZMP4VQgaX"
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
    }
}

base {
    archivesName.set("refinedstorage-emi-integration-neoforge")
}

val refinedstorageVersion: String by project
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
}
