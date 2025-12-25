package com.varunu28.paymentservice.health;

import com.varunu28.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("paymentServiceHealth")
public class PaymentServiceHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceHealthIndicator.class);

    private final PaymentRepository paymentRepository;

    public PaymentServiceHealthIndicator(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Health health() {
        LOGGER.info("Health check started");
        try {
            paymentRepository.count();
            return Health.up()
                .withDetail("database", "PostgreSQL connection healthy")
                .withDetail("service", "Payment service operational")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("database", "PostgreSQL connection failed")
                .withDetail("Error", e.getMessage())
                .withDetail("service", "Payment service not operational")
                .build();
        }
    }
}
