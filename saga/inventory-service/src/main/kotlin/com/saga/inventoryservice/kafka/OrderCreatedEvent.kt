package com.saga.inventoryservice.kafka

import kotlinx.serialization.Serializable

@Serializable
data class OrderCreatedEvent(
    val orderId: String,
    val inventoryCount: Int,
    val amount: Double,
    val customerName: String,
    val idempotencyKey: String
)