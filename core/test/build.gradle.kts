plugins {
    id("ara.library")
    id("ara.room")
}

android {
    namespace = "ara.note.test"
}

dependencies {

    implementation(project(":core:entity"))
    implementation(project(":core:util"))
    implementation(project(":core:database"))

    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.junit)
    implementation(libs.kotlinx.datetime)

    implementation(libs.androidx.test.rules)
    implementation(libs.hilt.android.testing)
}
