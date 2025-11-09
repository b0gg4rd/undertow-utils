# Undertow Utils

A library with **utils** for **Undertow**-based projects.

## Requirements

  - [Docker Engine](https://docs.docker.com/engine/install/)

## Building

```shell
docker run \
  --net=host \
  --rm \
  -w $(pwd) \
  -v $(pwd):$(pwd) \
  -v ${HOME}/.m2:${HOME}/.m2 \
  azul/zulu-openjdk-alpine:21.0.5 \
  ./mvnw -Djansi.force=true -ntp -U clean install
```


