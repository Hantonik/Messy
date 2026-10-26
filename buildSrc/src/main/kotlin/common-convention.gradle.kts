plugins {
    idea
}

tasks.withType<Test>().configureEach {
    failOnNoDiscoveredTests = false
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}