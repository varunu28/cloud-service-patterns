# cloud-service-patterns
Code accompanying blogs for cloud service patterns

## How to run?
#### To run Spring cloud config server for connecting over filesystem
Create a config file in filesystem on path `${user.home}/config`. Please note that file system based backend is good for development & testing purposes only. It is not recommended for production use unless you have a file system that is reliable for production.

## Automatically refresh config through Spring cloud bus & Spring cloud config monitor with filesystem based config backend
 - With filesystem based config backend, `spring-cloud-config-monitor` will automatically detect file system changes & refresh the config
 - We don't need to configure anything additional for config refresh to work like we did in case of Git based config backend
 - Start AMQP broker like RabbitMQ or Kafka
 - The Spring beans with the `@RefreshScope` annotation will be refreshed with the new configuration without restarting the application

## Testing locally
 - Run the services & config server locally 