plugins {
    id("java-convention")

    alias(libs.plugins.protobuf)
}

dependencies {
    implementation(libs.grpc.nettyShaded)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
}

protobuf {
    protoc {
        artifact = libs.protoc.get().toString()
    }

    plugins {
        create("grpc") {
            artifact = libs.grpc.protocGen.get().toString()
        }
    }

    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc") {}
            }
        }
    }
}

sourceSets.main {
    java {
        srcDirs(
            project.layout.buildDirectory.dir("generated/source/proto/main/grpc"),
            project.layout.buildDirectory.dir("generated/source/proto/main/java"),
        )
    }
}