plugins {
    id("kmp-convention")

    alias(libs.plugins.compose.compiler)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:data"))
            implementation(project(":shared:encryption"))
            implementation(project(":shared:network"))
            implementation(project(":shared:util"))

            implementation(libs.kotlinx.coroutines)

            implementation(libs.ktor.network)
            implementation(libs.ktor.utils)

            implementation(libs.cryptography.core)
            implementation(libs.cryptography.provider)
        }

        androidMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.components)
            implementation(libs.compose.ui)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.compose.ui.toolingPreview)
        }
    }
}