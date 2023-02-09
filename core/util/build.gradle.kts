plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "aranote.core.util"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = false
    }
}

dependencies {

    implementation(libs.kotlinx.datetime)
    implementation(libs.prettytime)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.android)
}
