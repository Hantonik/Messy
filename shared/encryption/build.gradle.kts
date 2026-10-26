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
            implementation(libs.cryptography.core)
            implementation(libs.cryptography.provider)
        }

        jvmMain.dependencies {
            compileOnly(libs.lombok)
        }
    }
}

dependencies {
    "jvmMainAnnotationProcessor"(libs.lombok)
}