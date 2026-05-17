package com.saga.paymentservice.kafka

import kotlinx.serialization.Serializable

@Serializable
data class PaymentEvent(
    val orderId: String
)
