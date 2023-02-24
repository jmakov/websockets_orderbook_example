cmake_minimum_required(VERSION 3.16.3)
project(ws_ob VERSION 1.0.0 LANGUAGES CXX)

# Disable C and C++ compiler extensions. C/CXX_EXTENSIONS are ON by default to
# allow the compilers to use extended variants of the C/CXX language. However,
# this could expose cross-platform bugs in user code or in the headers of
# third-party dependencies and thus it is strongly suggested to turn
# extensions off.
set(CMAKE_C_EXTENSIONS OFF)
set(CMAKE_CXX_EXTENSIONS OFF)

set(CMAKE_POSITION_INDEPENDENT_CODE ON)

# Location of additional cmake modules
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} ${PROJECT_SOURCE_DIR}/cmake)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED on)

set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)
set(CMAKE_SUPPRESS_DEVELOPER_WARNINGS ON)

# If you want to self-test benchmark lib too, turn me ON
set(BENCHMARK_ENABLE_TESTING OFF CACHE BOOL "Enable benchmark self-testing?")

# TODO: compiler optimizations: https://www.phoronix.com/scan.php?page=article&item=clang-12-opt&num=1
set(CLANG_WARNINGS
        "-Wall \
        -Wextra \
        -Wshadow \
        -Wnon-virtual-dtor \
        -Wold-style-cast \
        -Wcast-align \
        -Wunused \
        -Woverloaded-virtual \
        -Wpedantic \
        -Wconversion \
        -Wsign-conversion \
        -Wnull-dereference \
        -Wdouble-promotion \
        -Wformat=2"
        )
set(CMAKE_CXX_FLAGS "-Wall -march=native -pthread -pipe -DBUILD_STATIC_LIBS=ON -DBUILD_SHARED_LIBS=OFF")
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS} -g3 -fno-lto")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS} -DNDEBUG -Ofast -flto -funroll-loops -fomit-frame-pointer")

if (CMAKE_CXX_COMPILER_ID STREQUAL "GNU")
    set(GCC_WARNINGS
            "${CLANG_WARNINGS} \
            -Wmisleading-indentation \
            -Wduplicated-cond \
            -Wduplicated-branches \
            -Wlogical-op \
            -Wuseless-cast"
            )
elseif (CMAKE_CXX_COMPILER_ID STREQUAL "Clang")
    add_link_options("-fuse-ld=lld")
endif()


add_subdirectory(src)
