plugins {
    java
    application
    idea
    id("io.freefair.lombok") version "9.5.+" apply false

    id("net.neoforged.gradleutils") version "6.+"
}

gradleutils.version {
    tags {
        val versionLabel = prop("project_version_label")

        if (versionLabel != "release")
            label = versionLabel
    }
}

version = gradleutils.version.toString()
group = "hantonik.${prop("project_id")}"

println("${prop("project_name")}, Version: $version")

allprojects {
    pluginManager.apply("idea")

    version = rootProject.version
    group = rootProject.group
}

subprojects {
    pluginManager.apply("java")
    pluginManager.apply("application")
    pluginManager.apply("io.freefair.lombok")

    dependencies {
        val nettyVersion = prop("netty_version")

        implementation("io.netty:netty-buffer:${nettyVersion}")
        implementation("io.netty:netty-codec:${nettyVersion}")
        implementation("io.netty:netty-common:${nettyVersion}")
        implementation("io.netty:netty-handler:${nettyVersion}")
        implementation("io.netty:netty-transport:${nettyVersion}")
        implementation("io.netty:netty-transport-classes-epoll:${nettyVersion}")
        implementation("io.netty:netty-transport-native-epoll:${nettyVersion}:linux-aarch_64")
        implementation("io.netty:netty-transport-native-epoll:${nettyVersion}:linux-x86_64")
        implementation("io.netty:netty-transport-classes-kqueue:${nettyVersion}")
        implementation("io.netty:netty-transport-native-kqueue:${nettyVersion}:osx-aarch_64")
        implementation("io.netty:netty-transport-native-kqueue:${nettyVersion}:osx-x86_64")

        implementation("com.google.code.gson:gson:${prop("gson_version")}")
        implementation("com.google.guava:guava:${prop("guava_version")}") {
            exclude(group = "com.google.guava", module = "listenablefuture")
        }

        implementation("commons-io:commons-io:${prop("commons_io_version")}")
        implementation("org.apache.commons:commons-lang3:${prop("commons_lang3_version")}")

        val log4jVersion = prop("log4j_version")

        implementation("org.apache.logging.log4j:log4j-core:${log4jVersion}")
        implementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")
        implementation("org.apache.logging.log4j:log4j-slf4j2-impl:${log4jVersion}")

        implementation("org.slf4j:slf4j-api:${prop("slf4j_version")}")
    }

    tasks.withType<Test>().configureEach {
        failOnNoDiscoveredTests = false
    }

    tasks.processResources {
        from(rootProject.file("LICENSE.md")) {
            into(".")
        }
    }

    idea {
        module {
            isDownloadSources = true
            isDownloadJavadoc = true
        }
    }
}

fun prop(name: String, default: String? = null): String {
    return providers.gradleProperty(name).orElse(default ?: "").get()
}
