# Config Server

Seek `application.yml` for configuration sample.

## Important notes
1. Public SSH key must be defined in `~/.ssh/known_hosts` file (see comments in `application.yml`)

## Samples
1. Sample request for `core` application in `paganblacker` profile:
```sh
curl -XGET http://localhost:8888/core-paganblacker.properties
```
