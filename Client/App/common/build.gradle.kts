plugins {
    id("kmp-convention")

    kotlin("plugin.lombok")
}

kotlin {
    jvm()

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmVersion.get()))
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:data"))
            implementation(project(":shared:encryption"))
            implementation(project(":shared:network"))
            implementation(project(":shared:util"))

            implementation(libs.kotlinx.coroutines)

            implementation(libs.cryptography.core)
            implementation(libs.cryptography.provider)
        }

        jvmMain.dependencies {
            implementation(libs.google.guava)
            implementation(libs.commons.io)

            implementation(libs.log4j.api)
            implementation(libs.log4j.core)
            implementation(libs.log4j.slf4j2)
            implementation(libs.slf4j.api)

            compileOnly(libs.lombok)
        }
    }
}

dependencies {
    "jvmMainAnnotationProcessor"(libs.lombok)
}