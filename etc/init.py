#!/usr/bin/env python3

import requests
import cassandra.cluster
from cassandra import ConsistencyLevel
from cassandra.cluster import Cluster

__BE_CORE_BASE_URL = 'http://localhost:8080/core/v1'
__PROFILES = [
    {
        "title": "V",
        "birth": "1996-11-17",
        "gender": "FEMALE",
        "purposes": ["DATING", "RELATIONSHIPS"],
        "countryId": "RU"
    },
    {
        "title": "X",
        "birth": "1996-12-17",
        "gender": "FEMALE",
        "purposes": ["DATING", "SEXTING"],
        "countryId": "RU"
    },
    {
        "title": "Z",
        "birth": "1997-12-17",
        "gender": "FEMALE",
        "purposes": ["RELATIONSHIPS", "SEXTING"],
        "countryId": "RU"
    },
    {
        "title": "S",
        "birth": "1998-12-17",
        "gender": "FEMALE",
        "purposes": ["SEXTING"],
        "countryId": "BY"
    },
    {
        "title": "I",
        "birth": "1996-12-17",
        "gender": "MALE",
        "purposes": ["DATING"],
        "countryId": "RU"
    }
]


def create_profile_rest(profile) -> int:
    response = requests.post(f'{__BE_CORE_BASE_URL}/profiles', json=profile)
    return response.json()['id']


def generate_chats(cql_session, profile_id, profile_ids):
    opponents_ids = [p_id for p_id in profile_ids if p_id != profile_id]
    print(f"Opponents for {profile_id} are: {opponents_ids}")
    for opponent_id in opponents_ids:
        sorted_opponents = sorted([profile_id, opponent_id])
        persis_chatroom(cql_session, profile_id, opponent_id)


def persis_chatroom(cql_session, profile_id, opponent_id):
    contacts_by_user_ps = cql_session.prepare(
        "insert into contacts_by_user(user_id, contact_id, is_blocked, is_favourite, tags) values (?, ?, ?, ?, ?)")
    cql_session.execute(
        contacts_by_user_ps,
        (str(profile_id), str(opponent_id), False, False, {'friend'})
    )

if __name__ == '__main__':
    profile_ids = []
    cassandra_cluster = Cluster(['localhost'], port=9042)
    cassandra_session = cassandra_cluster.connect('chat')

    for profile in __PROFILES:
        profile_id = create_profile_rest(profile)
        profile_ids.append(profile_id)
        print(f"Profile {profile['title']} created with id: {profile_id}")

    for profile_id in profile_ids:
        generate_chats(cassandra_session, profile_id, profile_ids)

    print(f'Profile IDs: {profile_ids}')
