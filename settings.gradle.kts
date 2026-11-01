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
    "server:common:api", "server:common:grpc", "server:common:util",
    "server:core", "server:app", "server:web",
    "client:app:common",
    "client:app:mobile", "client:app:desktop:sidecar"
)