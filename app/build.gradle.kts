plugins {
    id("ara.application")
    id("ara.hilt")
    id("ara.room")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
}

android {
    namespace = "com.ara.aranote"

    defaultConfig {
        applicationId = "com.ara.aranote"
        versionCode = 1
        versionName = "0.0.1"

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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
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

    implementation(project(":core:entity"))
    implementation(project(":core:util"))
    testImplementation(project(":core:test"))
    androidTestImplementation(project(":core:test"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.google.android.material)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.datastore)

    // ### Jetpack Compose ####
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    // Tooling support (Previews, etc.)
    debugImplementation(libs.androidx.compose.ui.tooling)
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation(libs.androidx.compose.foundation)
    // Material Design
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowsize)
    // Material design icons
    implementation(libs.androidx.compose.material.iconsExtended)
    // Integration with activities
    implementation(libs.androidx.activity.compose)
    // Integration with ViewModels
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Compose Testing
    testImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.androidx.compose.ui.test)
    // Test rules and transitive dependencies:
    testImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    // Needed for createComposeRule, but not createAndroidComposeRule:
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // Animation
    implementation(libs.androidx.compose.animation)
    // Lifecycle
//    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.accompanist.navigation.animation)
//    androidTestImplementation "androidx.navigation:navigation-testing:$navigation_compose"

    // Hilt Dependency Injection
    kapt(libs.androidx.hilt.compiler)
    // Hilt Testing
    androidTestImplementation(libs.hilt.android.testing)

    // DateTime
    implementation(libs.kotlinx.datetime)
    implementation(libs.prettytime)

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
    testImplementation(libs.google.truth)
    androidTestImplementation(libs.google.truth)
    // Espresso dependencies
    testImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.espresso.core)
    // Other Test Libs
    testImplementation(libs.robolectric)

    // Other Libs
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)
    implementation(libs.karn.notify)
}
