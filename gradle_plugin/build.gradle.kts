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
        register("application") {
            id = "ara.application"
            implementationClass = "ApplicationPlugin"
        }
        register("hilt") {
            id = "ara.hilt"
            implementationClass = "HiltPlugin"
        }
    }
}
