plugins {
    id("ara.library.compose.feature")
}

android {
    namespace = "ara.note.navigation"
}

dependencies {
    implementation(project(":feature:settings"))
    implementation(project(":feature:notedetail"))
    implementation(project(":feature:home"))
    implementation(project(":feature:notebookslist"))

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.accompanist.navigation.animation)
//    androidTestImplementation "androidx.navigation:navigation-testing:$navigation_compose"
}
