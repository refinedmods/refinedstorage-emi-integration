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
    compileWithProject(project(":refinedstorage-emi-integration-common"))
    publishing {
        maven = true
    }
}

base {
    archivesName.set("refinedstorage-emi-integration-neoforge")
}

val refinedstorageVersion: String by project
val emiVersion: String by project

dependencies {
    api("com.refinedmods.refinedstorage:refinedstorage-platform-neoforge:${refinedstorageVersion}")
    runtimeOnly("dev.emi:emi-neoforge:${emiVersion}")
    compileOnlyApi("dev.emi:emi-neoforge:${emiVersion}")
}
