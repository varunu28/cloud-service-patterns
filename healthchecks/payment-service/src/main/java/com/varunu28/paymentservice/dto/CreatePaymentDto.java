package com.varunu28.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record CreatePaymentDto(
    @JsonProperty("idempotency_key") @NotBlank String idempotencyKey,
    @JsonProperty("amount") @Positive double amount,
    @JsonProperty("customer_id") @NotNull UUID customerId) {}
