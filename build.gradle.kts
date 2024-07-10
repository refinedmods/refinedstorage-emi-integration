plugins {
    id("refinedarchitect.root")
    id("refinedarchitect.base")
}

refinedarchitect {
    sonarQube("refinedmods_refinedstorage-emi-integration", "refinedmods")
}

subprojects {
    group = "com.refinedmods.refinedstorage"
}
