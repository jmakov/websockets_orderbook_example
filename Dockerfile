FROM ubuntu:jammy AS base_image
RUN apt-get update && apt-get -y install cmake g++ gdb libprotobuf-dev protobuf-compiler libssl-dev build-essential
COPY . /usr/src/websocket_orderbook_example
WORKDIR /usr/src/websocket_orderbook_example
RUN cd lib/libhv && rm -rf build && mkdir build && cd build && cmake .. -DCMAKE_BUILD_TYPE=Debug -DWITH_OPENSSL=ON -DBUILD_SHARED=OFF -DBUILD_STATIC=ON && cmake --build . -j $(nproc) && cmake --install .
RUN cd lib/rapidjson && rm -rf build && mkdir build && cd build && cmake .. -DCMAKE_BUILD_TYPE=Debug -DRAPIDJSON_BUILD_DOC=OFF -DRAPIDJSON_BUILD_EXAMPLES=OFF -DRAPIDJSON_BUILD_TESTS=OFF -DRAPIDJSON_BUILD_CXX17=ON && make . -j $(nproc) && make install

FROM base_image AS build_develop
RUN rm -rf build && mkdir build && cd build && cmake -DCMAKE_BUILD_TYPE=Debug .. && make all -j $(nproc)

FROM build_develop AS run_develop
ENTRYPOINT ["./build/bin/client_example"]

FROM base_image AS build_release
RUN cd lib/libhv && rm -rf build && mkdir build && cd build && cmake .. -DCMAKE_BUILD_TYPE=Release -DWITH_OPENSSL=ON -DBUILD_SHARED=OFF -DBUILD_STATIC=ON && cmake --build . -j $(nproc) && cmake --install .
RUN cd lib/rapidjson && rm -rf build && mkdir build && cd build && cmake .. -DCMAKE_BUILD_TYPE=Release -DRAPIDJSON_BUILD_DOC=OFF -DRAPIDJSON_BUILD_EXAMPLES=OFF -DRAPIDJSON_BUILD_TESTS=OFF -DRAPIDJSON_BUILD_CXX17=ON && make . -j $(nproc) && make install
RUN rm -rf build && mkdir build && cd build && cmake -DCMAKE_BUILD_TYPE=Release .. && make all -j $(nproc)

FROM build_release AS deployment
RUN rm -rf src

FROM deployment AS production
ENTRYPOINT ["./build/bin/client_example"]