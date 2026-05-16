package com.saga.inventoryservice.kafka

import com.saga.inventoryservice.kafka.KafkaTopics.ORDER_CREATED_TOPIC
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

val safeJsonParser = Json {
    ignoreUnknownKeys = true
}

@Component
class EventListeners {

    private val logger = LoggerFactory.getLogger(EventListeners::class.java)

    @KafkaListener(topics = [ORDER_CREATED_TOPIC], groupId = "order-created-inventory-group")
    fun orderCreatedListener(message: String, ack: Acknowledgment) {
        val orderCreatedEvent = safeJsonParser.decodeFromString<OrderCreatedEvent>(message)
        logger.info("Received message from order-created topic: {}", orderCreatedEvent)
        ack.acknowledge()
    }
}