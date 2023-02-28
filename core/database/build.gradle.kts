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

    testImplementation(libs.google.truth)
}
