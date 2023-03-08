plugins {
    id("ara.application")
    id("ara.hilt")
}

android {
    namespace = "ara.note"

    defaultConfig {
        applicationId = "ara.note"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunnerArguments["disableAnalytics"] = "true"
        testInstrumentationRunner = "com.ara.aranote.HTestRunner"

        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            isDebuggable = true
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs["debug"]
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    applicationVariants.all {
        val outputFileName = "AraNote_${versionName}.apk"
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl)
                .outputFileName = outputFileName
        }
    }

    packagingOptions {
        resources {
            // Multiple dependency bring these files in. Exclude them to enable
            // our test APK to build (has no effect on our AARs)
            excludes += "/META-INF/AL2.0"
            excludes += "/META-INF/LGPL2.1"

            // https://github.com/Kotlin/kotlinx.coroutines#avoiding-including-the-debug-infrastructure-in-the-resulting-apk
            excludes += "DebugProbesKt.bin"
        }
    }

    lint {
        checkDependencies = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        animationsDisabled = true
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }
}

tasks.withType<Test>().configureEach {
    systemProperties.put("robolectric.logging", "stdout")
}

dependencies {
    implementation(project(":feature:navigation"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)

    // Hilt Dependency Injection
    kapt(libs.androidx.hilt.compiler)
    // Hilt Testing
    androidTestImplementation(libs.hilt.android.testing)

    // Kotlinx Coroutines
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    // ### Unit and Instrumented Test ###
    testImplementation(libs.junit)
//    androidTestImplementation(libs.junit)
    testImplementation(libs.mockk)
//    testImplementation(libs.mockk.agent.api)
    testImplementation(libs.mockk.agent.jvm)
    testImplementation(libs.kotlin.reflect)
    // Androidx Test Core
    testImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.core.ktx)
    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    // Assertions
    testImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.ext.truth)
    // Other Test Libs
    testImplementation(libs.robolectric)

    // Other Libs
    implementation(libs.timber)
}
