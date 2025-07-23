# Undertow Utils

A library with **utils** for **Undertow**-based projects.

## Requirements

  - [Docker Engine](https://docs.docker.com/engine/install/)

## Building

```shell
docker run \
  -u $(id -u):$(grep docker /etc/group | awk -F\: '{print $3}') \
  --net=host \
  --rm \
  -w $(pwd) \
  -v /etc/group:/etc/group:ro \
  -v /etc/passwd:/etc/passwd:ro \
  -v $(pwd):$(pwd) \
  -v ${HOME}/.m2:${HOME}/.m2 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  azul/zulu-openjdk-alpine:21.0.5 \
  ./mvnw -Djansi.force=true -ntp -U clean install
```


