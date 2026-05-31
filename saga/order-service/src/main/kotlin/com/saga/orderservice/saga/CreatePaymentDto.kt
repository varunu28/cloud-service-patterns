package com.saga.orderservice.saga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePaymentDto(
    @SerialName("order_id") val orderId: String,
    val amount: Double,
    @SerialName("customer_name") val customerName: String,
    @SerialName("idempotency_key") val idempotencyKey: String,
)

