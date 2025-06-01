#!/bin/bash

# Build & load images into Minikube


# Delete existing resources if they exist under the namespace
kubectl delete namespace api-gateway --ignore-not-found

# Create a namespace for the API Gateway & set it as the current context
kubectl apply -f namespace.yml
kubectl config set-context --current --namespace=api-gateway

# Deploy Traefik
cd ./traefik
docker build -t varunu2892/traefik-custom-jwt:latest .
minikube image load varunu2892/traefik-custom-jwt:latest

# Apply Traefik RBAC
kubectl apply -f https://raw.githubusercontent.com/traefik/traefik/v2.11/docs/content/reference/dynamic-configuration/kubernetes-crd-definition-v1.yml
kubectl apply -f https://raw.githubusercontent.com/traefik/traefik/v2.11/docs/content/reference/dynamic-configuration/kubernetes-crd-rbac.yml
kubectl apply -f traefik-serviceaccount.yml
kubectl apply -f traefik-clusterrole.yml
kubectl apply -f traefik-clusterrolebinding.yml

# Create Traefik deployment and service
kubectl create configmap traefik-static-conf --from-file=traefik.yml=traefik.yml -n api-gateway --dry-run=client -o yaml | kubectl apply -f -

# Apply Traefik Deployment and Service
kubectl apply -f traefik-deployment.yml
kubectl apply -f traefik-service.yml

# Apply Traefik Middleware CRD instance
kubectl apply -f middleware.yml

cd ../gateway
# Install the Gateway API CRDs
kubectl apply -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v1.0.0/standard-install.yaml

# Create Gateway Class and Gateway resources
kubectl apply -f gateway.yml
kubectl apply -f gateway-class.yml

# Create HTTPRoute resources
kubectl apply -f httproute-order-pickup-service.yml
kubectl apply -f httproute-order-service.yml
kubectl apply -f httproute-user-service.yml

# Deploy services
cd ../services
kubectl apply -f order-pickup-service.yml
kubectl apply -f order-service.yml
# Create secret for user service
kubectl create secret generic jwt-secret --from-literal=secret='baff0426-74a0-4748-914b-b199b6b11104' -n api-gateway
kubectl apply -f user-service.yml

cd ..
