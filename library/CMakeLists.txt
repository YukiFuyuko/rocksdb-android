cmake_minimum_required(VERSION 3.10)
project(RocksDBWithDependencies)

# Disable warnings that break the build with -Werror
add_compile_options(-Wno-deprecated-copy -Wno-unused-but-set-variable -Wno-unused-variable -Wno-sign-compare -Wno-sync-alignment)

# Java version
set(CMAKE_JAVA_SOURCE_VERSION "8")
set(CMAKE_JAVA_TARGET_VERSION "8")

# LZ4
set(lz4_INCLUDE_DIRS "${CMAKE_CURRENT_SOURCE_DIR}/../lz4/lz4/lz4-1.10.0/lib")
set(lz4_LIBRARIES    "${CMAKE_CURRENT_SOURCE_DIR}/../lz4/build/intermediates/library_and_local_jars_jni/release/copyReleaseJniLibsProjectAndLocalJars/jni/${ANDROID_ABI}/liblz4.so")

# Snappy include directories
set(Snappy_INCLUDE_DIRS 
    "${CMAKE_CURRENT_SOURCE_DIR}/../snappy/snappy-1.2.2"
    "${CMAKE_CURRENT_SOURCE_DIR}/../snappy/build"
    "${CMAKE_CURRENT_SOURCE_DIR}/../snappy/build/include"
)
set(Snappy_LIBRARIES    "${CMAKE_CURRENT_SOURCE_DIR}/../snappy/build/intermediates/library_and_local_jars_jni/release/copyReleaseJniLibsProjectAndLocalJars/jni/${ANDROID_ABI}/libsnappy.so")

# BZip2
set(BZIP2_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../bz2/bzip2-1.0.8")
set(BZIP2_LIBRARIES   "${CMAKE_CURRENT_SOURCE_DIR}/../bz2/build/intermediates/library_and_local_jars_jni/release/copyReleaseJniLibsProjectAndLocalJars/jni/${ANDROID_ABI}/libbz2.so")

# ZSTD – corrected for RocksDB's Findzstd.cmake
set(zstd_INCLUDE_DIRS "${CMAKE_CURRENT_SOURCE_DIR}/../zstd/zstd-1.5.7/lib" CACHE PATH "zstd include directory")
set(zstd_LIBRARIES     "${CMAKE_CURRENT_SOURCE_DIR}/../zstd/build/intermediates/library_and_local_jars_jni/release/copyReleaseJniLibsProjectAndLocalJars/jni/${ANDROID_ABI}/libzstd.so" CACHE FILEPATH "zstd library")

# Include directories (optional, but helpful)
include_directories(${lz4_INCLUDE_DIRS})
include_directories(${Snappy_INCLUDE_DIRS})
include_directories(${zstd_INCLUDE_DIRS})

# Now build RocksDB
add_subdirectory(../rocksdb rocksdb-build)