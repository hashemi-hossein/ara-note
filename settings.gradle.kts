pluginManagement {
    includeBuild("gradle_plugin")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI("https://jitpack.io") }
    }
}

rootProject.name = "AraNote"
include(":app")
include(":core:entity")
include(":core:domain")
include(":core:ui")
include(":core:data")
include(":core:database")
include(":core:preference")
include(":core:util")
include(":core:backup")
include(":core:alarm")
include(":core:test")
include(":feature:settings")
include(":feature:notedetail")
