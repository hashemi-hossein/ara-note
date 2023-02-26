plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.androidGradlePlugin)
    compileOnly(libs.kotlinGradlePlugin)
    compileOnly(libs.kspGradlePlugin)
}

gradlePlugin {
    plugins {
        register("application") {
            id = "ara.application"
            implementationClass = "ApplicationPlugin"
        }
        register("library") {
            id = "ara.library"
            implementationClass = "LibraryPlugin"
        }
        register("library-compose") {
            id = "ara.library.compose"
            implementationClass = "LibraryComposePlugin"
        }
        register("library-compose-feature") {
            id = "ara.library.compose.feature"
            implementationClass = "LibraryComposeFeaturePlugin"
        }
        register("hilt") {
            id = "ara.hilt"
            implementationClass = "HiltPlugin"
        }
        register("room") {
            id = "ara.room"
            implementationClass = "RoomPlugin"
        }
    }
}
