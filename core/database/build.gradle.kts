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
    androidTestImplementation(project(":core:test"))

    implementation(libs.kotlinx.datetime)

    // Kotlinx Coroutines
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.google.truth)
    androidTestImplementation(libs.google.truth)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.hilt.android.testing)
}
