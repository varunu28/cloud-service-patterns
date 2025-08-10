# Circuit Breaker 
 
## Spring Circuit Breaker 

- Build images & deploy to Kubernetes
```bash
  ./deployment/run.sh
 ```

- Port forwarding for istio gateway
```bash
 kubectl port-forward svc/istio-ingressgateway -n istio-system 8080:80
```

 - Inject failure through config-map after changing value of `config.txt` to `FAIL`
 ```
  kubectl apply -f deployment/config-map.yml
 ```

![Basic Architecture](media/architecture.png)
