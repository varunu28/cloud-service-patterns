package com.varunu28.paymentservice.health;

import com.varunu28.paymentservice.repository.PaymentRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("paymentServiceDeepCheck")
public class PaymentServiceDeepCheckIndicator implements HealthIndicator {

    private final PaymentRepository paymentRepository;

    public PaymentServiceDeepCheckIndicator(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Health health() {
        try {
            Health.Builder builder;

            long startTime = System.currentTimeMillis();
            long count = paymentRepository.count();
            long endTime = System.currentTimeMillis();

            long duration = endTime - startTime;
            if (duration > 1000) {
                builder = Health.down()
                    .withDetail("warning", "Slow database response time")
                    .withDetail("count", count)
                    .withDetail("response_time_ms", duration)
                    .withDetail("service", "Payment service deep check operational but slow");
            } else {
                builder = Health.up()
                    .withDetail("database", "PostgreSQL connection healthy")
                    .withDetail("payment_count", count)
                    .withDetail("response_time_ms", duration)
                    .withDetail("service", "Payment service deep check operational");
            }
            return builder.build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("database", "PostgreSQL connection failed")
                .withDetail("Error", e.getMessage())
                .withDetail("service", "Payment service deep check not operational")
                .build();
        }
    }
}
