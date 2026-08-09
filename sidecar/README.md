# Sidecar Pattern

## Deploying sidecar with mTLS security in minikube cluster
```
cd deployment
./deploy.sh
```

## Testing
 - Enable HTTP port forwarding for `book-service-gateway-istio` to port `8080`
 - Test by running `curl http://localhost:8080/api/v1/books` 