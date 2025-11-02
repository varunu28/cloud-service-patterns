#!/bin/bash

# Build only the payment service image with simulated failure
cd ../payment-service
./mvnw spring-boot:build-image

## Return to root directory
cd ..

## Upload payment service image to minikube
minikube image load varunu2892/payment-service:faulty-service

# Deploy payment service
cd deployment
kubectl apply -f services/payment-service.yml