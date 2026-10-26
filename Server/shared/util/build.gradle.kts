plugins {
    id("java-convention")
}

dependencies {
    implementation(project(":shared:encryption"))
    implementation(project(":shared:util"))

    implementation(libs.google.guava)
    implementation(libs.commons.io)

    implementation(libs.log4j.api)
    implementation(libs.log4j.core)
    implementation(libs.log4j.slf4j2)
    implementation(libs.slf4j.api)
}