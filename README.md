# cloud-service-patterns
Code accompanying blogs for cloud service patterns

## How to run?
#### To run Spring cloud config server for connecting over GitHub
 - Create a personal access token in your [GitHub account](https://github.com/settings/personal-access-tokens)
 - Store the credentials in environment variables so that Spring can query them at runtime
```bash
    export SPRING_CLOUD_CONFIG_SERVER_GIT_USERNAME=your-username
    export SPRING_CLOUD_CONFIG_SERVER_GIT_PASSWORD=your-personal-access-token
```

## Manually refreshing config through Spring cloud bus
 - Expose the `/actuator/refresh` endpoint in your `application.properties` file for all services
 - Start AMQP broker like RabbitMQ or Kafka 
 - Make a `POST` request to the `/actuator/busrefresh` endpoint in Spring cloud config server
 - The Spring beans with the `@RefreshScope` annotation will be refreshed with the new configuration without restarting the application