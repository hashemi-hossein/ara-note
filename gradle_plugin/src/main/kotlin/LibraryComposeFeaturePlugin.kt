import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.project

class LibraryComposeFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("ara.library.compose")
                apply("ara.hilt")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    targetSdk = 33

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    testInstrumentationRunnerArguments["disableAnalytics"] = "true"
                }
            }

            dependencies {
                // Core Modules
                add("implementation", project(":core:entity"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:util"))
                add("implementation", project(":core:preference"))
                add("testImplementation", project(":core:test"))
                add("androidTestImplementation", project(":core:test"))

                // Kotlin Test
                add("testImplementation", kotlin("test"))
                add("androidTestImplementation", kotlin("test"))
            }
        }
    }
}
