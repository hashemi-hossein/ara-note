plugins {
    id("ara.library.compose")
    id("ara.hilt")
}

android {
    namespace = "ara.note.settings"
}

dependencies {

    implementation(project(":core:util"))
    implementation(project(":core:ui"))
    implementation(project(":core:preference"))
    implementation(project(":core:backup"))
}
