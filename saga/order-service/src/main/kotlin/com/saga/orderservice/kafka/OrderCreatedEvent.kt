package com.saga.orderservice.kafka

import java.math.BigInteger

data class OrderCreatedEvent(
    val orderId: BigInteger,
    val inventoryCount: Int,
    val amount: Double,
    val customerName: String,
    val idempotencyKey: String
)
