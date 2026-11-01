plugins {
    id("server-convention")
}

dependencies {
    implementation(project(":server:common:api"))

    implementation(libs.spring.grcpClient)
}