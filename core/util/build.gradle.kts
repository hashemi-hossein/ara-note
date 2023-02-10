plugins {
    id("ara.library")
}

android {
    namespace = "aranote.core.util"
}

dependencies {

    implementation(libs.kotlinx.datetime)
    implementation(libs.prettytime)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.android)
}
