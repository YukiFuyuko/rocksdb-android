@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
}

val bz2Version = "1.0.8"
val bz2Sha = "ab5a03176ee106d3f0fa90e381da478ddae405918153cca248e682cd0c4a2269"

group = "io.maryk.bz2"
version = bz2Version

val bz2Home = projectDir.resolve("bzip2-$bz2Version")

android {
    namespace = "bz2"
    compileSdk = 35
    ndkVersion = "27.1.12297006"
    defaultConfig {
        minSdk = 21
        externalNativeBuild {
            cmake {
                targets.add("bz2")
                arguments.addAll(
                    listOf(
                        "-DBZ2_PATH=${bz2Home.absolutePath}",
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

val downloadBz2 = tasks.register<Exec>("downloadBz2") {
    workingDir = projectDir
    commandLine("./downloadBz2.sh", bz2Version, bz2Sha)
}

tasks.withType<com.android.build.gradle.tasks.ExternalNativeBuildJsonTask> {
    dependsOn(downloadBz2)
}
