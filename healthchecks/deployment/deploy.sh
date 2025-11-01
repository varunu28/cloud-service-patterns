#!/bin/bash

kubectl apply -f namespace.yml
kubectl config set-context --current --namespace=healthcheck-demo

# Deploy infra components
cd infra

kubectl apply -f paymentdb-postgres-secret.yml
kubectl apply -f paymentdb-postgres-configmap.yml
kubectl apply -f paymentdb-postgres-deployment.yml

## Build service images
cd ../../payment-service
./mvnw spring-boot:build-image

cd ..

## Upload service images to minikube
minikube image load varunu2892/payment-service:latest

# Deploy services
cd deployment
kubectl apply -f services/payment-service.yml
