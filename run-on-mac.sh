#!/bin/bash

echo "Checking Docker..."
if ! command -v docker &> /dev/null; then
    echo "Docker not installed. Install Docker Desktop first."
    exit 1
fi

echo "Starting PostgreSQL database container..."
docker compose up -d

echo "Waiting for database to run..."
while ! nc -z localhost 5432 2>/dev/null; do
  sleep 1
done

echo "Database ready!"

echo "Running Vehicle Renter App (macOS)..."
mvn clean javafx:run

echo "App closed. Shutting down the database container..."
docker compose down

echo "Done."
