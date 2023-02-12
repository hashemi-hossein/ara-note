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
        register("room") {
            id = "ara.room"
            implementationClass = "RoomPlugin"
        }
    }
}
