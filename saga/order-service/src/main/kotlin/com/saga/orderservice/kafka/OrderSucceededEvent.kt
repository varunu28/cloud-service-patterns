package com.saga.orderservice.kafka

import kotlinx.serialization.Serializable

@Serializable
data class OrderSucceededEvent(val orderId: String)
