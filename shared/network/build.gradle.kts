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
            implementation(project(":shared:util"))

            implementation(libs.kotlinx.coroutines)

            implementation(libs.cryptography.core)
            implementation(libs.cryptography.provider)
        }

        mobileMain.dependencies {
            implementation(libs.ktor.network)
            implementation(libs.ktor.utils)
        }

        jvmMain.dependencies {
            implementation(libs.netty.buffer)
            implementation(libs.netty.codec)
            implementation(libs.netty.common)
            implementation(libs.netty.handler)
            implementation(libs.netty.transport)
            implementation(libs.netty.transport.classesEpoll)
            implementation(libs.netty.transport.classesKqueue)
            implementation(project.dependencies.variantOf(libs.netty.transport.nativeEpollLinux) { classifier("linux-aarch_64") })
            implementation(project.dependencies.variantOf(libs.netty.transport.nativeEpollLinux) { classifier("linux-x86_64") })
            implementation(project.dependencies.variantOf(libs.netty.transport.nativeKqueueOsx) { classifier("osx-aarch_64") })
            implementation(project.dependencies.variantOf(libs.netty.transport.nativeKqueueOsx) { classifier("osx-x86_64") })

            implementation(libs.google.guava)
            implementation(libs.commons.io)

            implementation(libs.slf4j.api)

            compileOnly(libs.lombok)
        }
    }
}

dependencies {
    "jvmMainAnnotationProcessor"(libs.lombok)
}