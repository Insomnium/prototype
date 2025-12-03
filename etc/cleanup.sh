#!/usr/bin/env bash

FS_IMAGES_FOLDER="./photo"
KEYCLOAK_BASE_URL="http://localhost:9090"
KEYCLOAK_ADMIN_REALM='master'
KEYCLOAK_REALM='prototype'
KEYCLOAK_ADMIN_CLIENT='admin-cli'
KEYCLOAK_ADMIN_USER='admin'
KEYCLOAK_ADMIN_PASSWORD='password'

echo "Cleaning up profiles from postgres"
docker exec postgres-prototype psql -U postgres -d core -c "delete from images"
docker exec postgres-prototype psql -U postgres -d core -c "delete from profiles"

echo "Removing ES profile index"
curl -XDELETE http://localhost:9200/profile

echo "Removing Kafka profile topic"
docker exec broker bash -c "kafka-topics --bootstrap-server localhost:9092 --topic profile --delete"

echo "Removing images"
find $FS_IMAGES_FOLDER -mindepth 1 -maxdepth 2 -type f ! -name '.gitkeep' -exec rm -f {} +

echo "Cleaning up cassandra chats"
docker exec cassandra-1 cqlsh -e "truncate chat.contacts_by_user"
docker exec cassandra-1 cqlsh -e "truncate chat.p2p_room"

echo "Removing keycloak users"
access_token=$(curl -XPOST "$KEYCLOAK_BASE_URL/realms/$KEYCLOAK_ADMIN_REALM/protocol/openid-connect/token" -H 'Content-Type: application/x-www-form-urlencoded' -d "client_id=$KEYCLOAK_ADMIN_CLIENT" -d "username=$KEYCLOAK_ADMIN_USER" -d "password=$KEYCLOAK_ADMIN_PASSWORD" -d 'grant_type=password' | jq -r '.access_token')
echo $access_token
#user_ids=$(curl -XGET "$KEYCLOAK_BASE_URL/admin/realms/$KEYCLOAK_REALM/users" -H "Authorization: Bearer $access_token" | jq -r --arg pattern "user_" '.[] | select(.username | startswith("user_")) | {id: .id, username: .username}')
#curl -XGET "$KEYCLOAK_BASE_URL/admin/realms/$KEYCLOAK_REALM/users" -H "Authorization: Bearer ${access_token}" -H 'Accept: application/json' | jq -r --arg pattern "user_" '.[] | select(.username | startswith("user_")) | {id: .id, username: .username}'
REALM_USER_IDS=$(curl -XGET "$KEYCLOAK_BASE_URL/admin/realms/$KEYCLOAK_REALM/users" -H "Authorization: Bearer ${access_token}" -H 'Accept: application/json' | jq -r '.[].id')
for USER_ID in $REALM_USER_IDS; do
    curl -XDELETE "$KEYCLOAK_BASE_URL/admin/realms/$KEYCLOAK_REALM/users/$USER_ID" -H "Authorization: Bearer $access_token"
    echo "Deregistered $USER_ID user from $KEYCLOAK_REALM keycloak realm"
done

echo "Done"
