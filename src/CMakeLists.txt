cmake_minimum_required(VERSION 3.16.3)
project(client_example)

find_package(OpenSSL REQUIRED)
set(OPENSSL_USE_STATIC_LIBS TRUE)

add_executable(${PROJECT_NAME} ClientExample.cpp)
target_link_libraries(${PROJECT_NAME} PRIVATE hv_static OpenSSL::Crypto OpenSSL::SSL pthread)