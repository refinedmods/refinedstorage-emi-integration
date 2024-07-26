plugins {
    id("refinedarchitect.common")
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
    common()
    publishing {
        maven = true
    }
}

base {
    archivesName.set("refinedstorage-emi-integration-common")
}

val refinedstorageVersion: String by project
val emiVersion: String by project

dependencies {
    api("com.refinedmods.refinedstorage:refinedstorage-common:${refinedstorageVersion}")
    compileOnlyApi("dev.emi:emi-xplat-mojmap:${emiVersion}")
}
