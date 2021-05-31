# Micronaut MuShop Docker

The complete **Micronaut MuShop** application can be run using `docker-compose` locally.

## Default Configuration

The default configuration that the `docker-compose` file uses is to be offline.

When using `docker-compose` some services are not available, such as publishing the newsletter which uses an Oracle Cloud Function, however the remaining services are functional.

## Prerequisites

You need Docker and Docker Composed installed locally and at least 8GB of resources assigned to Docker in order to run instances of Oracle database within containers.

## Quick Start

### Pull images
Start by pulling all necessary images. Note that it may take a little while to download all the images (in particular the Oracle Database image is 2GB to download).

```shell
# From this directory
docker-compose pull

# From the mushop root
docker-compose -f deploy/docker-compose/docker-compose.yml pull
```

### Run stack
After the images are downloaded start the stack in the detached state `-d` option.

```shell
# From this directory
docker-compose up -d

# From the mushop root
docker-compose -f deploy/docker-compose/docker-compose.yml up -d
```

Note that it may take a little while to spin up the containers.

You can track the progress of services starting up with `docker-compose ps` and once all services have a status of `Up` then open [http://localhost:81](http://localhost:81) in your browser to access MuShop.


## Shutdown

```shell
# From this directory
docker-compose down
# From the mushop root
docker-compose -f deploy/docker-compose/docker-compose.yml down
```
