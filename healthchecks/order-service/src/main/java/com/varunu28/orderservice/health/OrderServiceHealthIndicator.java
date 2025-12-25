package com.varunu28.orderservice.health;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varunu28.orderservice.repository.OrderRepository;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component("orderServiceHealth")
public class OrderServiceHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceHealthIndicator.class);

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RestClient restClient;
    @Value("${payment.healthcheck.url}")
    private String paymentHealthCheckUrl;

    public OrderServiceHealthIndicator(
        OrderRepository orderRepository, RabbitTemplate rabbitTemplate, RestClient restClient) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.restClient = restClient;
    }

    @Override
    public Health health() {
        LOGGER.info("Health check started");
        // check database connection
        try {
            orderRepository.count();
        } catch (Exception e) {
            LOGGER.info("Database health check failed: {}", e.getMessage());
            return Health.down()
                .withDetail("database", "PostgreSQL connection failed")
                .withDetail("Error", e.getMessage())
                .withDetail("service", "Order service not operational")
                .build();
        }
        // check RabbitMQ connection
        try {
            rabbitTemplate.convertAndSend("health.checks", "health.check", "ping");
        } catch (Exception e) {
            LOGGER.info("RabbitMQ health check failed: {}", e.getMessage());
            return Health.down()
                .withDetail("messageBroker", "RabbitMQ connection failed")
                .withDetail("Error", e.getMessage())
                .withDetail("service", "Order service not operational")
                .build();
        }
        // check Payment Service connection
        try {
            HealthCheckResponse healthCheckResponse = restClient.get()
                .uri(paymentHealthCheckUrl)
                .retrieve()
                .body(HealthCheckResponse.class);
            if (!"UP".equals(Objects.requireNonNull(healthCheckResponse).status())) {
                LOGGER.info("Payment Service health check reported DOWN status");
                return Health.down()
                    .withDetail("paymentService", "Payment Service reported DOWN status")
                    .withDetail("service", "Order service not operational")
                    .build();
            }
        } catch (Exception e) {
            LOGGER.info("Payment Service health check failed: {}", e.getMessage());
            return Health.down()
                .withDetail("paymentService", "Payment Service connection failed")
                .withDetail("Error", e.getMessage())
                .withDetail("service", "Order service not operational")
                .build();
        }
        LOGGER.info("Payment Service health check completed");
        return Health.up()
            .withDetail("database", "PostgreSQL connection healthy")
            .withDetail("messageBroker", "RabbitMQ connection healthy")
            .withDetail("paymentService", "Payment Service connection healthy")
            .withDetail("service", "Order service operational")
            .build();
    }

    private record HealthCheckResponse(
        @JsonProperty("status") String status, @JsonProperty("details") HealthCheckDetails details) {}

    private record HealthCheckDetails(
        @JsonProperty("database") String database, @JsonProperty("service") String service) {}
}
