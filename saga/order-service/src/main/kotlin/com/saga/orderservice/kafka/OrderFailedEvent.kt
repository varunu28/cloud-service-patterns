package com.saga.orderservice.kafka

import kotlinx.serialization.Serializable

@Serializable
data class OrderFailedEvent(val orderId: String)
