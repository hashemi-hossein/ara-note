plugins {
    id("ara.library")
    id("ara.room")
}

android {
    namespace = "aranote.core.database"
}

dependencies {

    implementation(project(":core:entity"))

    implementation(libs.kotlinx.datetime)

    // Kotlinx Coroutines
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
