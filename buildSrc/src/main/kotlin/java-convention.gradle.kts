import com.android.build.gradle.internal.lint.AndroidLintGlobalTask

plugins {
    id("common-convention")

    java
    kotlin("jvm")

    application

    id("io.freefair.lombok")
    kotlin("plugin.lombok")
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

java {
    toolchain.languageVersion = JavaLanguageVersion.of(libs.findVersion("jvmVersion").get().getRequiredVersion())
    modularity.inferModulePath = true

    withSourcesJar()
}

dependencies {
    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())
}

application {
    mainModule = "${rootProject.group}.${project.parent!!.name}.${project.name}"
}

tasks.processResources {
    from(rootProject.file("LICENSE.md")) {
        into(".")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Jar>().configureEach {
    archiveBaseName = "${rootProject.name}-${project.parent!!.name}-${project.name}"

    manifest {
        attributes["Specification-Title"] = archiveBaseName
        attributes["Specification-Vendor"] = "Hantonik"
        attributes["Specification-Version"] = "1"
        attributes["Implementation-Title"] = archiveBaseName
        attributes["Implementation-Version"] = rootProject.version
        attributes["Implementation-Vendor"] = "Hantonik"
        attributes["Automatic-Module-Name"] = application.mainModule.get()
        attributes["Multi-Release"] = "true"
    }
}