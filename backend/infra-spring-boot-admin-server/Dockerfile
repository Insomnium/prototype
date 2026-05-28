# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY ../../backend .
RUN mvn depencency:go-offline -B

COPY src ./src
RUN mvn clean package -pl infra-spring-boot-admin-server -am -DskipTests

# Runtime stage
FROM alpine/java:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
EXPOSE 1111
ENTRYPOINT ["java", "-jar", "app.jar"]
