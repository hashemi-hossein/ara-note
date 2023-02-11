plugins {
    id("ara.library")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
}

android {
    namespace = "aranote.core.entity"
}

dependencies {

    implementation(project(":core:util"))

    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.serialization.json)
}
