#!/bin/bash

cd insurance-service
./mvnw spring-boot:build-image

cd ../health-record-service
./mvnw spring-boot:build-image

cd ..
minikube image load varunu2892/insurance-service:latest
minikube image load varunu2892/health-record-service:latest
