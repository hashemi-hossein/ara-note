import ara.KOTLIN_ANDROID
import ara.getVersionCatalogLibs
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

class ApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply(KOTLIN_ANDROID)
            }
            
            val libs = getVersionCatalogLibs()
            
            extensions.configure<ApplicationExtension> {
                compileSdk = 33
                defaultConfig {
                    minSdk = 21
                }
                
                compileOptions {
                    // Flag to enable support for the new language APIs
                    isCoreLibraryDesugaringEnabled = true
                    
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }
                (this as ExtensionAware).extensions.configure<KotlinJvmOptions>("kotlinOptions") {
                    jvmTarget = JavaVersion.VERSION_11.toString()
    
                    freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
                }
    
                buildFeatures {
                    compose = true
                }
                composeOptions {
                    kotlinCompilerExtensionVersion =
                        libs.findVersion("compose.compiler").get().toString()
                }
            }
    
            dependencies {
                add("coreLibraryDesugaring", libs.findLibrary("core.jdk.desugaring").get())
    
                val composeBom = platform(libs.findLibrary("androidx.compose.bom").get())
                add("implementation", composeBom)
                add("androidTestImplementation", composeBom)
            }
        }
    }
}
