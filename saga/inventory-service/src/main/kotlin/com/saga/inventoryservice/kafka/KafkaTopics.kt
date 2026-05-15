package com.saga.inventoryservice.kafka

object KafkaTopics {
    const val ORDER_CREATED_TOPIC = "order-created"
    const val PAYMENT_SUCCEEDED_TOPIC = "payment-succeeded"
    const val PAYMENT_FAILED_TOPIC = "payment-failed"
    const val INVENTORY_RESERVATION_SUCCESSFUL_TOPIC = "inventory-reservation-successful"
    const val INVENTORY_RESERVATION_FAILED_TOPIC = "inventory-reservation-failed"
}