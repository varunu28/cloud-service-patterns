package com.saga.paymentservice.kafka

import kotlinx.serialization.Serializable

@Serializable
data class OrderCreatedEvent(
    val orderId: String,
    val amount: Double,
    val customerName: String,
    val idempotencyKey: String
)