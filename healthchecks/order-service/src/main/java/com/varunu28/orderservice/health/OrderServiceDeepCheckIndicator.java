package com.varunu28.orderservice.health;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varunu28.orderservice.service.OrderService;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component("orderServiceDeepCheck")
public class OrderServiceDeepCheckIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceDeepCheckIndicator.class);

    private final OrderService orderService;
    private final RestClient restClient;

    @Value("${test.prefix}")
    private String testPrefix;

    @Value("${payment.deepcheck.url}")
    private String paymentDeepCheckUrl;

    public OrderServiceDeepCheckIndicator(OrderService orderService, RestClient restClient) {
        this.orderService = orderService;
        this.restClient = restClient;
    }

    @Override
    public Health health() {
        LOGGER.info("Deep check started");
        try {
            // First, verify Payment Service deep check
            PaymentDeepCheckResponse paymentDeepCheckResponse = restClient.get()
                .uri(paymentDeepCheckUrl)
                .retrieve()
                .body(PaymentDeepCheckResponse.class);
            if (!"UP".equals(Objects.requireNonNull(paymentDeepCheckResponse).status())) {
                LOGGER.info("Deep check failed for Payment Service");
                return Health.down()
                    .withDetail("paymentDeepCheck", "Payment Service deep check reported DOWN status")
                    .withDetail("service", "Order service deep check failed")
                    .build();
            }
            // Perform a deep check by creating a test order
            UUID testOrderId = orderService.createOrder(
                testPrefix + UUID.randomUUID(),
                100.0,
                UUID.randomUUID());
            LOGGER.info("Deep check passed");
            return Health.up()
                .withDetail("id for test order", testOrderId.toString())
                .withDetail("service", "Order service deep check passed")
                .withDetail("paymentDeepCheck", paymentDeepCheckResponse.details.service)
                .build();
        } catch (Exception e) {
            LOGGER.info("Deep check failed due to exception: {}", e.getMessage());
            return Health.down()
                .withDetail("deepCheck", "Failed to create test order")
                .withDetail("Error", e.getMessage())
                .withDetail("service", "Order service deep check failed")
                .build();
        }
    }

    private record PaymentDeepCheckResponse(@JsonProperty("status") String status,
                                            @JsonProperty("details") DeepCheckDetails details) {}

    private record DeepCheckDetails(@JsonProperty("database") String database,
                                    @JsonProperty("payment_count") long paymentCount,
                                    @JsonProperty("response_time_ms") long responseTime,
                                    @JsonProperty("service") String service) {}
}
