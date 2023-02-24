# This shouldn't be used as `--target`. Here we just prepare the base image.
FROM drogonframework/drogon:latest AS base_image
# --- INSTALL DEPENDENCIES ---
# Boost required by packio
RUN add-apt-repository universe && apt-get update && apt-get install -y libboost-all-dev
COPY . /usr/src/websocket_orderbook_example
WORKDIR /usr/src/websocket_orderbook_example
# build msgpack required by packio
RUN cd lib/msgpack-c/ && rm -rf build && mkdir build && cd build && cmake -MSGPACK_USE_X3_PARSE=ON -MSGPACK_USE_STATIC_BOOST=ON .. && cmake --build . --target install -j $(nproc)

FROM base_image AS build_develop
RUN rm -rf build && mkdir build && cd build && cmake -DCMAKE_BUILD_TYPE=Develop .. && make all -j $(nproc)

FROM build_develop AS run_develop
CMD ["./build/bin/client_example"]

FROM base_image AS build_release
RUN rm -rf build && mkdir build && cd build && cmake -DCMAKE_BUILD_TYPE=Release .. && make all -j $(nproc)

FROM build_release AS deployment
RUN rm -rf src

FROM deployment AS production
CMD ["./build/bin/client_example"]