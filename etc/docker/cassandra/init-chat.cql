CREATE KEYSPACE IF NOT EXISTS chat WITH replication = { 'class': 'NetworkTopologyStrategy', 'prototype-dc1': 3 };
