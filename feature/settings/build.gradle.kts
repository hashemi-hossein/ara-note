plugins {
    id("ara.library.compose.feature")
}

android {
    namespace = "ara.note.settings"
}

dependencies {
    implementation(project(":core:backup"))
}
