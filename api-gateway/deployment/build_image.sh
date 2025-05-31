#!/bin/bash

# Build images
cd ../order-service
./mvnw spring-boot:build-image

cd ../order-pickup-service
./mvnw spring-boot:build-image

cd ../user-service
./mvnw spring-boot:build-image

cd ../deployment

# Load images into minikube
minikube image load varunu2892/order-service:latest
minikube image load varunu2892/order-pickup-service:latest
minikube image load varunu2892/user-service:latest