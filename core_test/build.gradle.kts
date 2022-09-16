plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.ara.core_test"
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
}

dependencies {

    implementation(project(":core"))

    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.junit)
    implementation(libs.kotlinx.datetime)
}
