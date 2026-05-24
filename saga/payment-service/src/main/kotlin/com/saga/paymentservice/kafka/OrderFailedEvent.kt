package com.saga.paymentservice.kafka

import kotlinx.serialization.Serializable

@Serializable
data class OrderFailedEvent(
    val orderId: String,
)
