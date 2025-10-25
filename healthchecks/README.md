# Health checks for Cloud Native Applications

## How to run?

#### Running the payment service
```bash
    cd payment-service
```
Start PostgreSQL using Docker Compose
```bash
    docker compose up
```

Set environment variables for database connection
```bash
    export POSTGRES_USER=payment_user
    export POSTGRES_PASSWORD=secret
```

Run the Spring Boot application
```bash
  ./mvnw spring-boot:run
```
