@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
}

val lz4Version = "1.10.0"

group = "io.maryk.lz4"
version = lz4Version

val lz4Home = projectDir.resolve("lz4/lz4-$lz4Version")

android {
    namespace = "lz4"
    compileSdk = 35
    ndkVersion = "27.1.12297006"
    defaultConfig {
        minSdk = 21
        externalNativeBuild {
            cmake {
                targets.add("liblz4")
                arguments.addAll(
                    listOf(
                        "-DLZ4_PATH=${lz4Home.absolutePath}/lib/",
                        "-DCMAKE_WARN_DEPRECATED=FALSE",
                        "-Wno-dev",
                    )
                )
            }
        }
        val archRaw = project.findProperty("arch") as? String ?: System.getProperty("arch")
        if (archRaw != null) {
            val aliasMap = mapOf(
                "arm" to "armeabi-v7a",
                "arm64" to "arm64-v8a",
                "x86_64" to "x86_64",
                "x86" to "x86"
            )
            val abiList = archRaw.split(",").map { it.trim() }.map { abi ->
                aliasMap[abi] ?: abi
            }
            ndk {
                abiFilters.clear()
                abiFilters.addAll(abiList)
            }
            externalNativeBuild.cmake.arguments.add("-DANDROID_ABI=${abiList.joinToString(";")}")
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    externalNativeBuild {
        cmake {
            path = File("$projectDir/CMakeLists.txt")
            version = "3.30.5"
        }
    }
}

val downloadLz4 = tasks.register<Exec>("downloadLz4") {
    workingDir = projectDir
    commandLine("./downloadLz4.sh", lz4Version)
}

tasks.withType<com.android.build.gradle.tasks.ExternalNativeBuildJsonTask> {
    dependsOn(downloadLz4)
}
