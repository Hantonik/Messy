import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("common-convention")

    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

val javaVersion = libs.findVersion("jvmVersion").get().requiredVersion

kotlin {
    android {
        namespace = "${rootProject.group}.${project.name}"

        compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
        minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()

        withJava()

        compilerOptions {
            jvmTarget.set(JvmTarget.valueOf("JVM_${javaVersion}"))
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.findLibrary("kotlin-logging").get())
        }

        val mobileMain = create("mobileMain") {
            dependsOn(commonMain.get())
        }

        androidMain.get().dependsOn(mobileMain)
        iosMain.get().dependsOn(mobileMain)
    }
}