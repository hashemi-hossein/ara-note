import ara.getVersionCatalogLibs
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class LibraryComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("ara.library")
            }

            val libs = getVersionCatalogLibs()

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }
                composeOptions {
                    kotlinCompilerExtensionVersion =
                        libs.findVersion("compose.compiler").get().toString()
                }
            }

            dependencies {
                // ### Jetpack Compose ####
                val composeBom = platform(libs.findLibrary("androidx.compose.bom").get())
                add("implementation", composeBom)
                add("androidTestImplementation", composeBom)
                //
                add("implementation", libs.findLibrary("androidx.compose.ui").get())
                add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())
                // Tooling support (Previews, etc.)
                add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
                // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
                add("implementation", libs.findLibrary("androidx.compose.foundation").get())
                // Material Design
                add("implementation", libs.findLibrary("androidx.compose.material").get())
                add("implementation", libs.findLibrary("androidx.compose.material3").get())
                add("implementation", libs.findLibrary("androidx.compose.material3.windowsize").get())
                // Material design icons
                add("implementation", libs.findLibrary("androidx.compose.material.iconsExtended").get())
                // Integration with activities
                add("implementation", libs.findLibrary("androidx.activity.compose").get())
                // Integration with ViewModels
                add("implementation", libs.findLibrary("androidx.lifecycle.viewmodel.compose").get())
                // Compose Testing
                add("testImplementation", libs.findLibrary("androidx.compose.ui.test").get())
                add("androidTestImplementation", libs.findLibrary("androidx.compose.ui.test").get())
                // Test rules and transitive dependencies:
                add("testImplementation", libs.findLibrary("androidx.compose.ui.test.junit4").get())
                add("androidTestImplementation", libs.findLibrary("androidx.compose.ui.test.junit4").get())
                // Needed for createComposeRule, but not createAndroidComposeRule:
                add("debugImplementation", libs.findLibrary("androidx.compose.ui.test.manifest").get())
                // Animation
                add("implementation", libs.findLibrary("androidx.compose.animation").get())
            }
        }
    }
}
