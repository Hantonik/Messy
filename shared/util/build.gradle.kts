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
            implementation(libs.kotlinx.coroutines)
        }

        jvmMain.dependencies {
            implementation(libs.google.guava)
            implementation(libs.slf4j.api)

            compileOnly(libs.lombok)
        }
    }
}

dependencies {
    "jvmMainAnnotationProcessor"(libs.lombok)
}