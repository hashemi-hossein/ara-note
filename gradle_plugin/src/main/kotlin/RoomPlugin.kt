import ara.getVersionCatalogLibs
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.process.CommandLineArgumentProvider
import java.io.File

class RoomPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
            }

            extensions.configure<KspExtension> {
                class RoomSchemaArgProvider(
                    @get:InputDirectory
                    @get:PathSensitive(PathSensitivity.RELATIVE)
                    val schemaDir: File
                ) : CommandLineArgumentProvider {
                    override fun asArguments(): Iterable<String> {
                        return listOf("room.schemaLocation=${schemaDir.path}")
                    }
                }

                arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
            }

            val libs = getVersionCatalogLibs()
            dependencies {
                add("implementation", libs.findLibrary("androidx.room.runtime").get())
                add("implementation", libs.findLibrary("androidx.room.ktx").get())
                add("ksp", libs.findLibrary("androidx.room.compiler").get())
                add("testImplementation", libs.findLibrary("androidx.room.testing").get())
                add("androidTestImplementation", libs.findLibrary("androidx.room.testing").get())
            }
        }
    }
}
