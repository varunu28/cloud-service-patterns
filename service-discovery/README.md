# Service Discovery on [Consul](https://developer.hashicorp.com/consul)

## How to run?
 - Run `docker-compose up -d` to start the `consul` container.
 - Start the `greeting-service` & `name-service`. Both will be registered on `consul`. This can be confirmed by visiting `localhost:8500` in your browser.
 - Start `webapp` service & it will be able to discover both `greeting-service` & `name-service` using `consul` service discovery.