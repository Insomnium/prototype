#!/usr/bin/env python3

from os import access
import requests
import cassandra.cluster
from cassandra import ConsistencyLevel
from cassandra.cluster import Cluster

__KEYCLOAK_DEBUG_SECRETS = {
    'client_id': 'admin-cli',
    'username': 'admin',
    'password': 'password',
    'grant_type': 'password'
}
__KEYCLOAK_BASE_URL = 'http://localhost:9090'
__KEYCLOAK_USER_ROLE = 'users'
__KEYCLOAK_REALM = 'prototype'
__KEYCLOAK_MASTER_REALM = 'master'
__BE_CORE_BASE_URL = 'http://localhost:8080/core/v1'
__PROFILES = [
    {
        "title": "Vika",
        "birth": "1996-11-17",
        "gender": "FEMALE",
        "purposes": ["DATING", "RELATIONSHIPS"],
        "countryId": "RU",
        "firstName": "Vika",
        "lastName": "Vikova"
    },
    {
        "title": "Xelen",
        "birth": "1996-12-17",
        "gender": "FEMALE",
        "purposes": ["DATING", "SEXTING"],
        "countryId": "RU",
        "firstName": "Xelen",
        "lastName": "Xelenova"
    },
    {
        "title": "Zaraza",
        "birth": "1997-12-17",
        "gender": "FEMALE",
        "purposes": ["RELATIONSHIPS", "SEXTING"],
        "countryId": "RU",
        "firstName": "Zaraza",
        "lastName": "Zarazova"
    },
    {
        "title": "Sarah",
        "birth": "1998-12-17",
        "gender": "FEMALE",
        "purposes": ["SEXTING"],
        "countryId": "BY",
        "firstName": "Sarah",
        "lastName": "Connor"
    },
    {
        "title": "Igor",
        "birth": "1996-12-17",
        "gender": "MALE",
        "purposes": ["DATING"],
        "countryId": "RU",
        "firstName": "Igor",
        "lastName": "Igorev"
    }
]


def backend_create_profile(profile) -> int:
    response = requests.post(f'{__BE_CORE_BASE_URL}/profiles', json=profile)
    return response.json()['id']


def keycloak_request_access_token() -> str:
    response = requests.post(f'{__KEYCLOAK_BASE_URL}/realms/{__KEYCLOAK_MASTER_REALM}/protocol/openid-connect/token',data=__KEYCLOAK_DEBUG_SECRETS)
    return response.json()['access_token']


def keycloak_build_auth_headers(access_token: str):
    return {
        'Content-Type': 'application/json',
        'Authorization': f'Bearer {access_token}'
    }


def keycloak_get_user_id(user_name, access_token):    
    response = requests.get(f"{__KEYCLOAK_BASE_URL}/admin/realms/{__KEYCLOAK_REALM}/users", headers=keycloak_build_auth_headers(access_token), params={'username': user_name})    
    return response.json()[0]['id']


def keycloak_register_user_entity(profile: dict, access_token: str):
    payload = {
        "username": profile['title'],
        "firstName": profile["firstName"],
        "lastName": profile["lastName"],
        "email": f"{profile['title']}@mail.org",
        "emailVerified": True,
        "enabled": True,
        "credentials": [
            {
                "type": "password",
                "value": "password",
                "temporary": True
            }
        ]
    }
    requests.post(
        f"{__KEYCLOAK_BASE_URL}/admin/realms/{__KEYCLOAK_REALM}/users",
        headers=keycloak_build_auth_headers(access_token),
        json=payload
    )
    
    
def keycloak_assign_user_role(user_id: str, role_id: str, role_name: str, access_token: str):
    requests.post(
        f"{__KEYCLOAK_BASE_URL}/admin/realms/{__KEYCLOAK_REALM}/users/{user_id}/role-mappings/realm", 
        headers=keycloak_build_auth_headers(access_token),
        json=[{ "id": role_id, "name": role_name }]
    )


def keycloak_get_role_id(role_name: str, access_token: str) -> str:
    response = requests.get(f"{__KEYCLOAK_BASE_URL}/admin/realms/{__KEYCLOAK_REALM}/roles/{role_name}", headers=keycloak_build_auth_headers(access_token))
    return response.json()['id']


def keycloak_create_user(profile: dict, access_token: str):
    keycloak_register_user_entity(profile, access_token)
    id = keycloak_get_user_id(profile['title'], access_token)
    users_role_id = keycloak_get_role_id(__KEYCLOAK_USER_ROLE, access_token)
    keycloak_assign_user_role(id, users_role_id, __KEYCLOAK_USER_ROLE, access_token)


def generate_chats(cql_session, profile_id, profile_ids):
    opponents_ids = [p_id for p_id in profile_ids if p_id != profile_id]
    print(f"Opponents for {profile_id} are: {opponents_ids}")
    for opponent_id in opponents_ids:
        sorted_opponents = sorted([profile_id, opponent_id])
        persist_chatroom(cql_session, sorted_opponents[0], sorted_opponents[1])


def persist_chatroom(cql_session, profile_id, opponent_id):
    contacts_by_user_ps = cql_session.prepare(
        "insert into contacts_by_user(user_id, contact_id, is_blocked, is_favourite, tags) values (?, ?, ?, ?, ?)")
    cql_session.execute(
        contacts_by_user_ps,
        (str(profile_id), str(opponent_id), False, False, {'friend'})
    )
    
def establish_cassandra_connection():
    cassandra_cluster = Cluster(['localhost'], port=9042)
    return cassandra_cluster.connect('chat')

if __name__ == '__main__':
    profile_ids = []
    cassandra_session = establish_cassandra_connection()
    keycloak_access_token = keycloak_request_access_token()

    for profile in __PROFILES:
        # Register core backend profile
        profile_id = backend_create_profile(profile)        
        profile_ids.append(profile_id)
        
        # Create Keycloak user
        keycloak_create_user(profile, keycloak_access_token)
        print(f"Profile {profile['title']} created with id: {profile_id}")

    # Pre-generate chats (all-to-all)
    for profile_id in profile_ids:
        generate_chats(cassandra_session, profile_id, profile_ids)

    print(f'Profile IDs: {profile_ids}')
