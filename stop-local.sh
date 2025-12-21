#!/bin/bash

echo "Stopping Docker Kafka..."
docker compose -f docker-compose.kafka.yml down

echo "Killing local Spring Boot apps..."
pkill -f spring-boot

echo "All services stopped"
