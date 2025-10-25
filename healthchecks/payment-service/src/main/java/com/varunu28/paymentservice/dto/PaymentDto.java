package com.varunu28.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varunu28.paymentservice.model.Payment;
import java.util.UUID;

public record PaymentDto(
    @JsonProperty("id") UUID id,
    @JsonProperty("idempotency_key") String idempotencyKey,
    @JsonProperty("amount") double amount,
    @JsonProperty("customer_id") UUID customerId) {

    public static PaymentDto fromPayment(Payment payment) {
        return new PaymentDto(
            payment.getId(),
            payment.getIdempotencyKey(),
            payment.getAmount(),
            payment.getCustomerId()
        );
    }
}
