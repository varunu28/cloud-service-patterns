#!/bin/bash

# Build images
cd ../greeting-service
./mvnw spring-boot:build-image

cd ../name-service
./mvnw spring-boot:build-image

cd ../webapp
./mvnw spring-boot:build-image

cd ../deployment