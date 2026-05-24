package com.saga.orderservice.kafka

import kotlinx.serialization.Serializable

@Serializable
data class InventoryEvent(
    val orderId: String,
)
