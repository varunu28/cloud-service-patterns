#!/bin/bash

kubectl apply -f namespace.yml
kubectl config set-context --current --namespace=healthcheck-demo

# Deploy infra components
cd infra

kubectl apply -f paymentdb-postgres-secret.yml
kubectl apply -f paymentdb-postgres-configmap.yml
kubectl apply -f paymentdb-postgres-deployment.yml
kubectl apply -f order-service-secret.yml
kubectl apply -f orderdb-postgres-configmap.yml
kubectl apply -f orderdb-postgres-deployment.yml
kubectl apply -f order-service-rabbitmq-deployment.yml

## Build service images
cd ../../payment-service
./mvnw spring-boot:build-image
cd ../order-service
./mvnw spring-boot:build-image

## Return to root directory
cd ..

## Upload service images to minikube
minikube image load varunu2892/payment-service:latest
minikube image load varunu2892/order-service:latest

# Deploy services
cd deployment
kubectl apply -f services/payment-service.yml
kubectl apply -f services/order-service.yml
