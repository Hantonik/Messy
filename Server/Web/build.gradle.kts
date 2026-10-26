plugins {
    id("server-convention")
}

dependencies {
    implementation(project(":server:shared:api"))

    implementation(libs.spring.grcpClient)
}