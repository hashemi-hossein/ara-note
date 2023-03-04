plugins {
    id("ara.library")
}

android {
    namespace = "ara.note.util"

    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.prettytime)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    coreLibraryDesugaring(libs.core.jdk.desugaring)
}
