plugins {
    kotlin("jvm") apply false

    alias(libs.plugins.compose.compiler) apply false
    id("org.jetbrains.kotlin.multiplatform") apply false
    id("com.android.kotlin.multiplatform.library") apply false
    id("com.android.application") apply false

    id("net.neoforged.gradleutils") version "6.+"
}

gradleutils.version {
    tags {
        val versionLabel = libs.versions.project.label.get()

        if (versionLabel != "release")
            label = versionLabel
    }
}

version = gradleutils.version.toString()
group = "hantonik.${rootProject.name.lowercase()}"

println("${rootProject.name}, Version: $version")