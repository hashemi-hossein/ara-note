plugins {
    id("ara.library")
    id("ara.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ara.note.preference"
}

dependencies {
    implementation(project(":core:util"))

    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.javax.inject)

    // Kotlinx Coroutines
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
