package com.saga.inventoryservice.kafka

import kotlinx.serialization.Serializable

@Serializable
data class InventoryEvent(
    val orderId: String,
)
