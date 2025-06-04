# Motivation
Boredom.
The first idea came to mind is a dating service. Why? Да хз.

# Backend
Currently consists of `core` service: 
* Postgres as a primary DB
* ElasticSearch: indexing and searching performance improvement
* Kafka to asynchronously index profiles being created

## Plans
* Core service extension with support for basic business features like profile editing, including photos uploading (CDN?)
* Spring Cloud Gateway based API Gateway (security, observability, underlying services scalability)
* Dedicated chatting service
* Observability (logging and tracing at least)

## Setup

### Run locally

1. Use `docker-compose.yml` from the root folder to setup local infrastructure
2. Build and run core service
    1. Navigate `backend/core/core` folder
    2. Build service via `./mvnw clean package`
    3. Run it by executing `java -jar target/{path-to-executable-jar} --spring.profiles.active=local`
    4. Check out APIs (e.g. using Bruno collection from _etc/.bruno_ folder in the root of the project)
    5. Track results in DB, ES, Kafka (see [Local env URIs](#-local-env-uris) section)    
   
Makefile is to be done

# Frontend
One day maybe

# Local env URIs  
[ElasticVue](http://localhost:8085/)  
[Kafka Control Center](http://localhost:9021/)

# Important note
I've got another username, which've I used in a couple of commits from another machine, Github considers them as authored by another GH account. That's a freaking coincidence.

