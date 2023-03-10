plugins {
    id("ara.library")
    id("ara.hilt")
}

android {
    namespace = "ara.note.alarm"
}

dependencies {
    implementation(project(":core:entity"))
    implementation(project(":core:repository"))
    implementation(project(":core:util"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.karn.notify)
}
