# Sidecar Pattern

## Deploying sidecar with mTLS security in minikube cluster
```
cd deployment
./deploy.sh
```

## Testing
 - Enable HTTP port forwarding for `book-service-gateway-istio` to port `8080`
 - Test by running `curl http://localhost:8080/api/v1/books`

## Testing for mTLS
 - Create a `test-client` pod in default namespace so that no Istio sidecar gets injected
```
kubectl run test-client \
  --namespace=default \
  --image=curlimages/curl:latest \
  --restart=Never \
  -- sleep 3600
```
 - Then invoke the forwarded endpoint for `book-service` by running below command
 ```
 kubectl exec -it test-client -n default -- curl -i -v http://book-service.sidecar.svc.cluster.local/api/v1/books
 ```
 - Connection will be reset by the sidecar container as its a plain text HTTP request & not a TLS handshake
 - This issue gets resolved if the `test-client` container is created in same namespace i.e. `sidecar` as Istio will
 inject a sidecar for encrypting the egress traffic.