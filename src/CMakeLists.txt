cmake_minimum_required(VERSION 3.16.3)
project(client_example)

# disable nlohmann_json tests
set(JSON_BuildTests OFF CACHE INTERNAL "")

set(Boost_USE_STATIC_LIBS ON)
set(Boost_USE_MULTITHREADED ON)
set(Boost_USE_STATIC_RUNTIME ON)

find_package(Boost REQUIRED COMPONENTS)

add_executable(${PROJECT_NAME} ClientExample.cpp)

target_include_directories(${PROJECT_NAME} PRIVATE ../lib/json/include)
target_include_directories(${PROJECT_NAME} PRIVATE ../lib/packio/include)
