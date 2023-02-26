plugins {
    id("ara.library.compose.feature")
    id("ara.hilt")
}

android {
    namespace = "ara.note.home"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.datetime)

    androidTestImplementation(libs.google.truth)
}
