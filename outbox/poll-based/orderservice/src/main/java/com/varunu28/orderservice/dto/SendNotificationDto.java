package com.varunu28.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record SendNotificationDto(
    @NotBlank @JsonProperty("idempotency_key") String idempotencyKey,
    @NotBlank @JsonProperty("event_type") String eventType,
    @NotBlank @JsonProperty("customer_name") String customerName,
    @Positive @JsonProperty("order_id") long orderId,
    @Positive double amount) {
}