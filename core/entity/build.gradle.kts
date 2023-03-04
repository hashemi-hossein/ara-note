plugins {
    id("ara.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ara.note.entity"
}

dependencies {
    implementation(project(":core:util"))

    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.serialization.json)
}
