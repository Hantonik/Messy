plugins {
    id("java-convention")

    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

dependencies {
    implementation(project(":shared:data"))
    implementation(project(":shared:encryption"))
    implementation(project(":shared:util"))

    implementation(project(":server:common:grpc"))
    implementation(project(":server:common:util"))

    implementation(libs.findLibrary("google-guava").get())
    implementation(libs.findLibrary("commons-io").get())

    implementation(libs.findLibrary("cryptography-core-jvm").get())
    implementation(libs.findLibrary("cryptography-provider-jvm").get())

    implementation(libs.findLibrary("log4j-api").get())
    implementation(libs.findLibrary("log4j-core").get())
    implementation(libs.findLibrary("log4j-slf4j2").get())
    implementation(libs.findLibrary("slf4j-api").get())
}