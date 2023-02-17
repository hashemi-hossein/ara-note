plugins {
    id("ara.library")
}

android {
    namespace = "ara.note.util"
}

dependencies {

    implementation(libs.kotlinx.datetime)
    implementation(libs.prettytime)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
}
