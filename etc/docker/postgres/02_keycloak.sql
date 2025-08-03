-- Create Keycloak database and user
-- Create Keycloak database and user
CREATE DATABASE keycloak;
CREATE USER app_keycloak WITH PASSWORD 'keycloak_password';

-- Connect to the database
\c keycloak

-- Grant privileges
GRANT ALL PRIVILEGES ON SCHEMA public TO app_keycloak;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO app_keycloak;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO app_keycloak;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO app_keycloak;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON SEQUENCES TO app_keycloak;
