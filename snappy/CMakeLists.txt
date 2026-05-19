cmake_minimum_required (VERSION 3.10)

add_subdirectory(snappy-1.2.2 snappy)

# RocksDB uses RTTI for snappy::Sink (typeinfo symbol), but upstream snappy
# forces -fno-rtti. Re-enable RTTI for this target to satisfy shared linking.
if(TARGET snappy)
  target_compile_options(snappy PRIVATE "$<$<COMPILE_LANGUAGE:CXX>:-frtti>")
endif()

# Expose generated snappy-stubs-public.h for downstream (RocksDB) includes.
set(SNAPPY_STUBS_HEADER "${CMAKE_CURRENT_BINARY_DIR}/snappy/snappy-stubs-public.h")
if(NOT EXISTS "${SNAPPY_STUBS_HEADER}")
  set(SNAPPY_STUBS_HEADER "${CMAKE_CURRENT_BINARY_DIR}/snappy-stubs-public.h")
endif()

if(EXISTS "${SNAPPY_STUBS_HEADER}")
  file(MAKE_DIRECTORY "${CMAKE_SOURCE_DIR}/build/include")
  file(COPY "${SNAPPY_STUBS_HEADER}" DESTINATION "${CMAKE_SOURCE_DIR}/build/include")
else()
  message(FATAL_ERROR "Could not find generated snappy-stubs-public.h in ${CMAKE_CURRENT_BINARY_DIR}")
endif()
