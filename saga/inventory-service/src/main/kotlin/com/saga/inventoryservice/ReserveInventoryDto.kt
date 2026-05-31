package com.saga.inventoryservice

import com.fasterxml.jackson.annotation.JsonProperty

data class ReserveInventoryDto(
    @JsonProperty("customer_name") val customerName: String,
    @JsonProperty("inventory_count") val inventoryCount: Int,
    @JsonProperty("order_id") val orderId: String,
)
