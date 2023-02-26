# A simple websocket client processing market data

## Usage
```bash
DOCKER_BUILDKIT=1 docker build --target production -t production:0.9 .
```
## Design decisions
### Project
- multi stage Docker build enabling development (and debugging) in the Docker image

## Development
### CLion
#### Docker toolbox
In case "Services" toolbox reports permission denied errors: 
```bash
sudo chown $USER:docker /var/run/docker.sock
```

### Developing the app in the docker
CLion toolchains can be extended with a Docker type. It requires a Docker image:
```bash
DOCKER_BUILDKIT=1 docker build --target run_develop -t run_develop:latest .
```
Add a new CMake profile (Settings -> Build -> CMake -> Add) e.g. "Debug-Docker" and select the Docker toolchain. 

To make the IDE aware which toolchain to use (and which indices/libs it should parse), change the "Run/Debug Configuration"
to the configuration we have just created "Debug-Docker". After the IDE updates its index, we see the libs built in
the Docker image can now be imported. 

## TODO
- send logs to Clickhouse for real time visualization
- add percentile showing benchmarks e.g. [CppBenchmark](https://github.com/chronoxor/CppBenchmark)
- use Hummingbot in another Docker container to simulate how agents (with default Hummingbot settings) respond to
  - liquidity evaporation
  - trend ignition
  - quote stuffing (stalling the exchange & other market participants having to process more data than we i.e. slowing them down)
  - discovering block orders
