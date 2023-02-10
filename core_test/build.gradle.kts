plugins {
    id("ara.library")
}

android {
    namespace = "com.ara.core_test"
}

dependencies {
    
    implementation(project(":core:entity"))
    implementation(project(":core:util"))
    
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.junit)
    implementation(libs.kotlinx.datetime)
}
