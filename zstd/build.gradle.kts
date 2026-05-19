@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
}

val zstdVersion = "1.5.7"
val zstdSha = "eb33e51f49a15e023950cd7825ca74a4a2b43db8354825ac24fc1b7ee09e6fa3"

group = "io.maryk.zstd"
version = zstdVersion

val zstdHome = projectDir.resolve("zstd-$zstdVersion")

android {
    namespace = "zstd"
    compileSdk = 35
    ndkVersion = "27.1.12297006"
    defaultConfig {
        minSdk = 21
        externalNativeBuild {
            cmake {
                targets.add("libzstd_shared")
                arguments.addAll(
                    listOf(
                        "-DZSTD_BUILD_SHARED=ON",
                        "-DCMAKE_SHARED_LINKER_FLAGS=-Wl,-soname,libzstd.so",
                        "-DCMAKE_PLATFORM_NO_VERSIONED_SONAME=ON",
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
            path = File("$projectDir/zstd-${zstdVersion}/build/cmake/CMakeLists.txt")
            version = "3.30.5"
        }
    }
}

val downloadZstd = tasks.register<Exec>("downloadZstd") {
    workingDir = projectDir
    commandLine("./downloadZstd.sh", zstdVersion, zstdSha)
}

tasks.withType<com.android.build.gradle.tasks.ExternalNativeBuildJsonTask> {
    dependsOn(downloadZstd)
}
