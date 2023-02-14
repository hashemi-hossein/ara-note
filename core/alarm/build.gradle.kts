plugins {
    id("ara.library")
    id("ara.hilt")
}

android {
    namespace = "aranote.core.alarm"
}

dependencies {

    implementation(project(":core:data"))
    implementation(project(":core:util"))

    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.karn.notify)
}
