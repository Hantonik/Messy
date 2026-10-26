plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")

    implementation("com.android.tools.build:gradle:${libs.versions.agp.get()}")

    implementation(plugin(libs.plugins.spring.framework))
    implementation(plugin(libs.plugins.spring.dependencyManagement))

    implementation(plugin(libs.plugins.lombok))
    implementation(plugin(libs.plugins.kotlin.lombok))
}

fun plugin(plugin: Provider<PluginDependency>): Provider<String> {
    return plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
}