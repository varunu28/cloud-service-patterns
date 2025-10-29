package com.varunu28.orderservice.health;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varunu28.orderservice.service.OrderService;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component("orderServiceDeepCheck")
public class OrderServiceDeepCheckIndicator implements HealthIndicator {

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
        try {
            // First, verify Payment Service deep check
            PaymentDeepCheckResponse paymentDeepCheckResponse = restClient.get()
                .uri(paymentDeepCheckUrl)
                .retrieve()
                .body(PaymentDeepCheckResponse.class);
            if (!"UP".equals(Objects.requireNonNull(paymentDeepCheckResponse).status())) {
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
            return Health.up()
                .withDetail("id for test order", testOrderId.toString())
                .withDetail("service", "Order service deep check passed")
                .withDetail("paymentDeepCheck", paymentDeepCheckResponse.details.service)
                .build();
        } catch (Exception e) {
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
