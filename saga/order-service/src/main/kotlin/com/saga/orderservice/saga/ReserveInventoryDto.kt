package com.saga.orderservice.saga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReserveInventoryDto(
    @SerialName("customer_name") val customerName: String,
    @SerialName("inventory_count") val inventoryCount: Int,
    @SerialName("order_id") val orderId: String,
)
