plugins {
    id("ara.library.compose")
    id("ara.hilt")
}

android {
    namespace = "ara.note.notedetail"
}

dependencies {

    implementation(project(":core:domain"))
    implementation(project(":core:entity"))
    implementation(project(":core:util"))
    implementation(project(":core:ui"))
    implementation(project(":core:preference"))
    implementation(project(":core:alarm"))
    androidTestImplementation(project(":core:test"))

    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    implementation(libs.kotlinx.datetime)

    androidTestImplementation(libs.google.truth)
}
