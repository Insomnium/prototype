#!/usr/bin/env bash

FS_IMAGES_FOLDER="./photo"

echo "Cleaning up profiles from postgres"
docker exec postgres-prototype psql -U postgres -c "delete from images"
docker exec postgres-prototype psql -U postgres -c "delete from profiles"

echo "Removing ES profile index"
curl -XDELETE http://localhost:9200/profile

echo "Removing Kafka profile topic"
docker exec broker bash -c "kafka-topics --bootstrap-server localhost:9092 --topic profile --delete"

echo "Removing images"
find $FS_IMAGES_FOLDER -mindepth 1 -maxdepth 2 -type f ! -name '.gitkeep' -exec rm -f {} +

echo "Done"
