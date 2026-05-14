package com.saga.orderservice

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateOrderRequest(
    @JsonProperty("customer_name") val customerName: String,
    val amount: Double,
    @JsonProperty("inventory_count") val inventoryCount: Int
)
