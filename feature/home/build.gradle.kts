plugins {
    id("ara.library.compose")
    id("ara.hilt")
}

android {
    namespace = "ara.note.home"
}

dependencies {

    implementation(project(":core:domain"))
    implementation(project(":core:entity"))
    implementation(project(":core:util"))
    implementation(project(":core:ui"))
    implementation(project(":core:preference"))

    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.datetime)
}
