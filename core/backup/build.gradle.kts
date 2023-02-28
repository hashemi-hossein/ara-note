plugins {
    id("ara.library")
    id("ara.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ara.note.backup"
}

dependencies {

    implementation(project(":core:entity"))
    implementation(project(":core:repository"))
    implementation(project(":core:util"))

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.serialization.json)

    // Kotlinx Coroutines
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
