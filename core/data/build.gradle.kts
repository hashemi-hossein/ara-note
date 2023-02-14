plugins {
    id("ara.library")
}

android {
    namespace = "aranote.core.data"
}

dependencies {

    implementation(project(":core:entity"))
    implementation(project(":core:util"))
    implementation(project(":core:database"))

    // Kotlinx Coroutines
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
