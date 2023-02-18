plugins {
    id("ara.library")
    id("ara.hilt")
}

android {
    namespace = "ara.note.repository"
}

dependencies {

    implementation(project(":core:entity"))
    implementation(project(":core:util"))
    implementation(project(":core:database"))

    implementation(libs.kotlinx.datetime)

    // Kotlinx Coroutines
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
