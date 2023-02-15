plugins {
    id("ara.library.compose")
    id("ara.hilt")
}

android {
    namespace = "aranote.feature.notebookslist"
}

dependencies {

    implementation(project(":core:domain"))
    implementation(project(":core:entity"))
    implementation(project(":core:util"))
    implementation(project(":core:ui"))
    implementation(project(":core:preference"))
}
