pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }

        maven { url = uri("https://maven.neoforged.net/releases/") }

        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Messy"

include(
    "Server", "Server:App", "Server:Core", "Server:Web",
    "Client", "Client:Android", "Client:Core", "Client:Desktop",
//  "Client:Android:App", "Client:Android:Network", "Client:Android:UI",
    "Common"
)

project(":Client").projectDir = file("Client/App")
project(":Client:Android").projectDir = file("Client/App/Android")
project(":Client:Core").projectDir = file("Client/App/Core")
project(":Client:Desktop").projectDir = file("Client/App/Desktop/Java")

//project(":Client:Android:App").projectDir = file("Client/App/Android/App")
//project(":Client:Android:Network").projectDir = file("Client/App/Android/Network")
//project(":Client:Android:UI").projectDir = file("Client/App/Android/UI")