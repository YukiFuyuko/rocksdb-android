@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "io.maryk.rocksdb"
version = "10.10.1"

android {
    namespace = "org.rocksdb"
    compileSdk = 35
    ndkVersion = "27.1.12297006"
    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                arguments.addAll(
                    arrayOf(
                        "-DWITH_GFLAGS=NO",
                        "-DCMAKE_SYSTEM_NAME=Android",
                        "-DCMAKE_POSITION_INDEPENDENT_CODE=ON",
                        "-DWITH_TESTS=OFF",
                        "-DANDROID_STL=c++_shared",
                        "-DPORTABLE=ON",
                        "-DWITH_ZLIB=ON",
                        "-DWITH_LZ4=ON",
                        "-DWITH_ZSTD=ON",
                        "-DWITH_SNAPPY=ON",
                        "-DWITH_BZ2=ON",
                        "-DWITH_TESTS=OFF",
                        "-DWITH_TOOLS=OFF",
                        "-DWITH_JNI=ON",
                        "-Wno-error",
                        "-DCMAKE_PLATFORM_NO_VERSIONED_SONAME=ON",
                        "-DCMAKE_WARN_DEPRECATED=FALSE",
                        "-Wno-dev",
                    )
                )
                targets.add("rocksdbjni-shared")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    lint {
        lintConfig = file("lint.xml")
    }
    sourceSets {
        this["main"].run {
            java.directories.add("../rocksdb/java/src/main/java")
            jniLibs.srcDirs(
                "../bz2/build/intermediates/library_and_local_jars_jni/release/copyReleaseJniLibsProjectAndLocalJars/jni",
                "../lz4/build/intermediates/library_and_local_jars_jni/release/copyReleaseJniLibsProjectAndLocalJars/jni",
                "../snappy/build/intermediates/library_and_local_jars_jni/release/copyReleaseJniLibsProjectAndLocalJars/jni",
                "../zstd/build/intermediates/library_and_local_jars_jni/release/copyReleaseJniLibsProjectAndLocalJars/jni"
            )
        }
        this["androidTest"].run {
            java.directories.add("src/androidTest/kotlin")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
}

tasks.whenTaskAdded {
    if(name.startsWith("configureCMake")) {
        dependsOn(":lz4:copyReleaseJniLibsProjectAndLocalJars")
        dependsOn(":snappy:copyReleaseJniLibsProjectAndLocalJars")
        dependsOn(":bz2:copyReleaseJniLibsProjectAndLocalJars")
        dependsOn(":zstd:copyReleaseJniLibsProjectAndLocalJars")
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
}

mavenPublishing {
    coordinates(artifactId = "rocksdb-android")

    pom {
        name.set("rocksdb-android")
        description.set("Android RocksDB library")
        inceptionYear.set("2019")
        url.set("https://github.com/marykdb/rocksdb-android")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("jurmous")
                name.set("Jurriaan Mous")
                url.set("https://github.com/jurmous/")
            }
        }

        scm {
            url.set("https://github.com/marykdb/rocksdb-android")
            connection.set("scm:git:git://github.com/marykdb/rocksdb-android.git")
            developerConnection.set("scm:git:ssh://git@github.com/marykdb/rocksdb-android.git")
        }
    }
}


tasks.matching { it.name == "mergeReleaseJniLibFolders" }.configureEach {
    dependsOn(":lz4:copyReleaseJniLibsProjectAndLocalJars")
    dependsOn(":snappy:copyReleaseJniLibsProjectAndLocalJars")
    dependsOn(":bz2:copyReleaseJniLibsProjectAndLocalJars")
    dependsOn(":zstd:copyReleaseJniLibsProjectAndLocalJars")
}
