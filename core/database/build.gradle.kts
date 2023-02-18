plugins {
    id("ara.library")
    id("ara.room")
    id("ara.hilt")
}

android {
    namespace = "ara.note.database"
}

dependencies {

    implementation(project(":core:entity"))

    implementation(libs.kotlinx.datetime)

    // Kotlinx Coroutines
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
