# Service Discovery on Kubernetes

## How to run on `minikube`?
 - Switch to deployment directory `cd deployment`
 - Create a new namespace
```bash
  kubectl apply -f namespace.yml 
 ```
 - Build images for the services
```bash
    ./build_image.sh
```
 - Apply deployment files
```bash 
    kubectl apply -f greeting-service.yml 
    kubectl apply -f name-service.yml 
    kubectl apply -f webapp.yml
```
 - Run `minikube tunnel` to expose the `webapp` service
 - Now you should be able to access the `webapp` API at `http://localhost/api/v1/web`
 - Finally cleanup by running `kubectl delete namespace service-discovery`