plugins {
    id("server-convention")
}

dependencies {
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
}