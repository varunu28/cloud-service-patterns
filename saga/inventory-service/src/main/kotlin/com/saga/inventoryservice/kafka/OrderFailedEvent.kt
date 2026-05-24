package com.saga.inventoryservice.kafka

import kotlinx.serialization.Serializable

@Serializable
data class OrderFailedEvent(
    val orderId: String,
)
