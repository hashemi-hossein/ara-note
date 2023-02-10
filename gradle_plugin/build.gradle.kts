plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.androidGradlePlugin)
    compileOnly(libs.kotlinGradlePlugin)
}

gradlePlugin {
    plugins {
        register("library") {
            id = "ara.library"
            implementationClass = "LibraryPlugin"
        }
    }
}
