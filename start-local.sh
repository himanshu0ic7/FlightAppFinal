#!/bin/bash

# Absolute base directory of the project
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"

# Load environment variables
source "$BASE_DIR/.env"

echo "Starting Kafka & Zookeeper in Docker..."
docker compose -f "$BASE_DIR/docker-compose.kafka.yml" up -d

echo "Waiting for Kafka..."
sleep 20

echo "Starting Config Server..."
cd "$BASE_DIR/configServer" && mvn spring-boot:run &
sleep 8

echo "Starting Eureka Server..."
cd "$BASE_DIR/eurekaServer" && mvn spring-boot:run &
sleep 10

echo "Starting Flight Service..."
cd "$BASE_DIR/flightService" && mvn spring-boot:run &
sleep 5

echo "Starting Booking Service..."
cd "$BASE_DIR/bookingService" && mvn spring-boot:run &
sleep 5

echo "Starting Notification Service..."
cd "$BASE_DIR/notificationService" && mvn spring-boot:run &
sleep 5

echo "Starting Security Service..."
cd "$BASE_DIR/securityService" && mvn spring-boot:run &
sleep 5

echo "Starting API Gateway..."
cd "$BASE_DIR/applicationGateway" && mvn spring-boot:run &

echo "All services started successfully!"