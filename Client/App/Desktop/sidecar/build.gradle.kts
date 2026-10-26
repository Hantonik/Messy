plugins {
    id("java-convention")
}

dependencies {
    implementation(project(":shared:data"))
    implementation(project(":shared:encryption"))
    implementation(project(":shared:network"))
    implementation(project(":shared:util"))

    implementation(libs.netty.buffer)
    implementation(libs.netty.codec)
    implementation(libs.netty.common)
    implementation(libs.netty.handler)
    implementation(libs.netty.transport)
    implementation(libs.netty.transport.classesEpoll)
    implementation(libs.netty.transport.classesKqueue)
    implementation(variantOf(libs.netty.transport.nativeEpollLinux) { classifier("linux-aarch_64") })
    implementation(variantOf(libs.netty.transport.nativeEpollLinux) { classifier("linux-x86_64") })
    implementation(variantOf(libs.netty.transport.nativeKqueueOsx) { classifier("osx-aarch_64") })
    implementation(variantOf(libs.netty.transport.nativeKqueueOsx) { classifier("osx-x86_64") })

    implementation(libs.google.guava)
    implementation(libs.commons.io)

    implementation(libs.cryptography.core.jvm)
    implementation(libs.cryptography.provider.jvm)

    implementation(libs.log4j.api)
    implementation(libs.log4j.core)
    implementation(libs.log4j.slf4j2)
    implementation(libs.slf4j.api)
}

application {
    mainModule = "${rootProject.group}.client.${project.parent!!.name}.${project.name}"
}

tasks.withType<Jar>().configureEach {
    archiveBaseName = "${rootProject.name}-client-${project.parent!!.name}-${project.name}"

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

tasks.register<Copy>("copyExternalLibs") {
    description = "Task that copies external libraries"

    from(configurations.runtimeClasspath.get().filter {
        it.name.endsWith(".jar") && !it.name.contains(rootProject.name)
    })

    into(layout.buildDirectory.dir("out/library"))
}

tasks.register<Copy>("copyProjectJars") {
    description = "Task that copies project jars"

    dependsOn("jar")
    dependsOn(project(":shared:data").tasks.getByName("jvmJar"))
    dependsOn(project(":shared:encryption").tasks.getByName("jvmJar"))
    dependsOn(project(":shared:network").tasks.getByName("jvmJar"))
    dependsOn(project(":shared:util").tasks.getByName("jvmJar"))

    from(tasks.jar)
    from(project(":shared:data").tasks.getByName("jvmJar"))
    from(project(":shared:encryption").tasks.getByName("jvmJar"))
    from(project(":shared:network").tasks.getByName("jvmJar"))
    from(project(":shared:util").tasks.getByName("jvmJar"))

    into(layout.buildDirectory.dir("out/app"))
}

tasks.register("bundle") {
    description = "Bundle task for copying external libraries and project jars"

    dependsOn("copyExternalLibs")
    dependsOn("copyProjectJars")
}

tasks.register<Exec>("generateRuntime") {
    description = "Task that generates runtime image"

    dependsOn("bundle")

    commandLine(
        "jlink",
        "--module-path",
        listOf(
            layout.buildDirectory.dir("/out/app").get().asFile.absolutePath,
            layout.buildDirectory.dir("/out/library").get().asFile.absolutePath,
            "${System.getProperty("java.home")}/jmods"
        ).joinToString(File.pathSeparator),

        "--add-modules",
        listOf(
            "java.base",
            "java.compiler",
            "java.rmi",
            "java.scripting",
            application.mainModule.get()
        ).joinToString(","),

        "--output",
        layout.buildDirectory.dir("out/runtime").get().asFile.absolutePath,

        "--strip-debug",
        "--no-header-files",
        "--no-man-pages",
        "--compress=2"
    )
}