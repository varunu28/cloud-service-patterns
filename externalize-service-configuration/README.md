# cloud-service-patterns
Code accompanying blogs for cloud service patterns

## How to run?
#### Allow minikube to read images from local docker registry
```shell
  eval $(minikube docker-env)
```

#### Build docker images & load to minikube
```shell
  cd deployment
  ./build.sh
```

#### Apply service deployments
```shell
  kubectl apply -f deployment/name-service.yml
  kubectl apply -f deployment/greeting-service.yml
  kubectl apply -f deployment/webapp.yml
```

#### Set port forwarding for `webapp` service
```shell
  kubectl port-forward svc/webapp 8000:8000
```

#### Clean up
```shell
    kubectl delete deployment webapp && \
    kubectl delete deployment greeting-service && \
    kubectl delete deployment name-service
```
