package com.varunu28.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varunu28.orderservice.model.Order;
import java.time.LocalDateTime;

public record OrderDto(
    long id,
    @JsonProperty("customer_name") String customerName,
    double amount,
    String status,
    @JsonProperty("created_at") LocalDateTime createdAt) {

    public static OrderDto from(Order order) {
        return new OrderDto(
            order.getId(), order.getCustomerName(), order.getAmount(), order.getStatus(), order.getCreatedAt());
    }
}
