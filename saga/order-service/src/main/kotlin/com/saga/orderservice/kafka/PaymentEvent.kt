package com.saga.orderservice.kafka

import kotlinx.serialization.Serializable

@Serializable
data class PaymentEvent(
    val orderId: String,
)
