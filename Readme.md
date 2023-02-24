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
This can be used for debugging.

## TODO
- send logs to Clickhouse for real time visualization
- add percentile showing benchmarks e.g. [CppBenchmark](https://github.com/chronoxor/CppBenchmark)
- use Hummingbot in another Docker container to simulate how agents (with default Hummingbot settings) respond to
  - liquidity evaporation
  - trend ignition
  - quote stuffing (stalling the exchange & other market participants having to process more data than we i.e. slowing them down)
  - discovering block orders
