# Circuit Breaker 
 
## Spring Circuit Breaker 
 - Build image & upload image to minikube
```bash
  ./deployment/build_images.sh
 ```
 - Create new namespace
```bash
  kubectl apply -f deployment/namespace.yml
```
 - Apply config map
```bash
  kubectl apply -f deployment/config-map.yml
```
 - Deploy services
```bash
  kubectl apply -f deployment/health-record-service.yml
  kubectl apply -f deployment/insurance-service.yml
```
 - Inject failure through config-map after changing value of `config.txt` to `FAIL`
 ```
  kubectl apply -f deployment/config-map.yml
 ```

![Basic Architecture](media/architecture.png)