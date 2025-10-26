package com.varunu28.paymentservice.health;

import com.varunu28.paymentservice.repository.PaymentRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("paymentServiceHealth")
public class PaymentServiceHealthIndicator implements HealthIndicator {

    private final PaymentRepository paymentRepository;

    public PaymentServiceHealthIndicator(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Health health() {
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
