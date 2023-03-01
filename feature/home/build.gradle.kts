plugins {
    id("ara.library.compose.feature")
}

android {
    namespace = "ara.note.home"
}

dependencies {
    implementation(libs.kotlinx.datetime)
}
