#!/bin/bash

# Build images
cd ../greeting-service
./mvnw spring-boot:build-image

cd ../name-service
./mvnw spring-boot:build-image

cd ../webapp
./mvnw spring-boot:build-image

cd ../deployment

# Push images to Docker Hub
docker push varunu2892/greeting-service:latest
docker push varunu2892/name-service:latest
docker push varunu2892/webapp:latest

# Remove existing images from minikube
minikube image rm varunu2892/greeting-service:latest
minikube image rm varunu2892/name-service:latest
minikube image rm varunu2892/webapp:latest

# Load images into minikube
minikube image load varunu2892/greeting-service:latest
minikube image load varunu2892/name-service:latest
minikube image load varunu2892/webapp:latest
