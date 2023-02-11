import ara.KOTLIN_KAPT
import ara.getVersionCatalogLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("dagger.hilt.android.plugin")
                apply(KOTLIN_KAPT)
            }

            val libs = getVersionCatalogLibs()
            dependencies {
                add("implementation", libs.findLibrary("hilt.android").get())
                add("kapt", libs.findLibrary("hilt.android.compiler").get())
                add("kaptAndroidTest", libs.findLibrary("hilt.android.compiler").get())
            }
        }
    }
}
