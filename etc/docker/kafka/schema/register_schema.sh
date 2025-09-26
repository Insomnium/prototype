#!/usr/bin/env sh

echo starting
while ! nc -z schema-registry 8081; do
  echo "Waiting for Schema Registry..."
  sleep 5
done

function print_head() {
    echo ''
    echo '########################################################################################################'
    echo $1
    echo '########################################################################################################'
    echo ''
}

# Register all schemas in the schemas directory
for f in /schema/payload/*.json; do
  subject=$(basename "$f" .json)
  print_head "Registering schema from file $f. Subject: $subject"
  curl -XPOST -H 'Content-Type: application/json' --data-binary @"$f" http://schema-registry:8081/subjects/$subject/versions -v
  curl -XPUT -H 'Content-Type: application/json' -d '{ "compatibility": "BACKWARD" }' http://schema-registry:8081/config/$subject -v
done
