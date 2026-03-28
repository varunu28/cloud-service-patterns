package com.varunu28.orderservice.outbox;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PayloadData(
    double amount,
    @JsonProperty("customer_name") String customerName,
    @JsonProperty("order_id") String orderId) {
}
