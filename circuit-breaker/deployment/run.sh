#!/bin/bash

cd insurance-service
./mvnw spring-boot:build-image

cd ../health-record-service
./mvnw spring-boot:build-image

cd ..
minikube image load varunu2892/insurance-service:latest
minikube image load varunu2892/health-record-service:latest

# Create namespace
kubectl apply -f deployment/namespace.yml

# Apply config map to allow failure injection
kubectl apply -f deployment/config-map.yml -n circuit-breaker

# Apply service deployments
kubectl apply -f deployment/insurance-service.yml -n circuit-breaker
kubectl apply -f deployment/health-record-service.yml -n circuit-breaker

# Apply Istio resources
kubectl apply -f deployment/gateway.yml -n circuit-breaker
kubectl apply -f deployment/insurance-service-istio.yml -n circuit-breaker