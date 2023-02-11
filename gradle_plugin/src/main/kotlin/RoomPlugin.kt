import ara.KOTLIN_KAPT
import ara.getVersionCatalogLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class RoomPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(KOTLIN_KAPT)
            }
            
            val libs = getVersionCatalogLibs()
            dependencies {
                add("implementation", libs.findLibrary("androidx.room.runtime").get())
                add("implementation", libs.findLibrary("androidx.room.ktx").get())
                add("kapt", libs.findLibrary("androidx.room.compiler").get())
                add("testImplementation", libs.findLibrary("androidx.room.testing").get())
                add("androidTestImplementation", libs.findLibrary("androidx.room.testing").get())
            }
        }
    }
}
