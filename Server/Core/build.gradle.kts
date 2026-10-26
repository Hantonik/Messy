plugins {
    id("server-convention")
}

dependencies {
    implementation(libs.spring.data)
    implementation(libs.postgresql.driver)

    implementation(libs.spring.grpcServer)
}