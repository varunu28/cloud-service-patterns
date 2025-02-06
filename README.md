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

## Automatically refresh config through Spring cloud bus & Spring cloud config monitor
 - Expose the `/monitor` endpoint in your `application.properties` file for spring cloud config server
 - If you are using Spring security, you will need to enable the `/monitor` endpoint in your security configuration
 - Start AMQP broker like RabbitMQ or Kafka 
 - Create a [GitHub webhook](https://github.com/varunu28/cloud-service-patterns/settings/hooks) to trigger the `/monitor` endpoint to:
   - Send a `POST` request to the `/monitor` endpoint in Spring cloud config server
   - The Spring beans with the `@RefreshScope` annotation will be refreshed with the new configuration without restarting the application

## Testing locally
 - Expose `localhost:8888`(Spring cloud config server) to public network through [ngrok](https://ngrok.com/)
 - Use the ngrok exposed URL in configuring GitHub webhook