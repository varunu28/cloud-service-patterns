package com.saga.paymentservice

import com.fasterxml.jackson.annotation.JsonProperty

data class CreatePaymentDto(
    @JsonProperty("order_id") val orderId: String,
    val amount: Double,
    @JsonProperty("customer_name") val customerName: String,
    @JsonProperty("idempotency_key") val idempotencyKey: String,
)
