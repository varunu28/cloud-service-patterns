package com.saga.paymentservice.kafka

object KafkaTopics {
    const val ORDER_CREATED_TOPIC = "order-created"
    const val PAYMENT_SUCCEEDED_TOPIC = "payment-succeeded"
    const val PAYMENT_FAILED_TOPIC = "payment-failed"
}