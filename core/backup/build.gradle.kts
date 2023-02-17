plugins {
    id("ara.library")
    id("ara.hilt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
}

android {
    namespace = "ara.note.backup"
}

dependencies {

    implementation(project(":core:entity"))
    implementation(project(":core:data"))
    implementation(project(":core:util"))

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.serialization.json)

    // Kotlinx Coroutines
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
