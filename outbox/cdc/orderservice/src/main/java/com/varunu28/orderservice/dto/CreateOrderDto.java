package com.varunu28.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateOrderDto(
    @JsonProperty("customer_name") @NotBlank String customerName,
    @Positive double amount) {
}
