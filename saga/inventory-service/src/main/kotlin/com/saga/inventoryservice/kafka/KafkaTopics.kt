package com.saga.inventoryservice.kafka

object KafkaTopics {
    const val ORDER_CREATED_TOPIC = "order-created"
    const val INVENTORY_RESERVATION_SUCCESSFUL_TOPIC = "inventory-reservation-successful"
    const val INVENTORY_RESERVATION_FAILED_TOPIC = "inventory-reservation-failed"
}