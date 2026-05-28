#!/usr/bin/env bash

docker compose down
docker rm -f $(docker ps -aq)
docker volume rm -f $(docker volume ls -q)
docker network rm -f $(docker network ls -q)
docker volume prune -f
#docker compose --profile cassandra up

