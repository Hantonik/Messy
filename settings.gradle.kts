pluginManagement {
    repositories {
        maven { url = uri("https://maven.neoforged.net/releases/") }

        gradlePluginPortal()
        google()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "Messy"

include(
    "shared:data", "shared:encryption", "shared:network", "shared:util",
    "server:shared:api", "server:shared:grpc", "server:shared:util",
    "server:core", "server:app", "server:web",
    "client:app:mobile", "client:app:desktop:sidecar"
)