#!/usr/bin/env sh

while ! nc -z schema-registry 8081; do
  echo "Waiting for Schema Registry..."
  sleep 5
done

# Register all schemas in the schemas directory
for f in /schema/payload/*.json; do
  subject=$(basename "$f" .json)
  echo "Registering schema from file $f. Subject: $subject"
  curl -XPOST -H 'Content-Type: application/json' --data-binary @"$f" http://schema-registry:8081/subjects/$subject/versions -v
  curl -XPUT -H 'Content-Type: application/json' -d '{ "compatibility": "BACKWARD" }' http://schema-registry:8081/config/$subject -v
done


