plugins {
    id("ara.library")
}

android {
    namespace = "aranote.core.domain"
}

dependencies {

    implementation(project(":core:entity"))
    implementation(project(":core:data"))
    implementation(project(":core:util"))
    implementation(project(":core:preference"))

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.android)
}
