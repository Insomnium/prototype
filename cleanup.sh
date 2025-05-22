#!/usr/bin/env bash

echo "Cleaning up profiles from postgres"
docker exec postgres-prototype psql -U postgres -c "delete from public.profiles"

echo "Removing ES profile index"
curl -XDELETE http://localhost:9200/profile

echo "Removing Kafka profile topic"
docker exec broker bash -c "kafka-topics --bootstrap-server localhost:9092 --topic profile --delete"

echo "Done"
