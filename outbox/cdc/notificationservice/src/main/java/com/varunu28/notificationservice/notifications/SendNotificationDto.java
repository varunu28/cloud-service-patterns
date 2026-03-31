package com.varunu28.notificationservice.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendNotificationDto(
    @JsonProperty("idempotency_key") String idempotencyKey,
    @JsonProperty("event_type") String eventType,
    @JsonProperty("customer_name") String customerName,
    @JsonProperty("order_id") long orderId,
    double amount) {
}
