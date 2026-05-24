package com.saga.inventoryservice.kafka

import com.saga.inventoryservice.InventoryService
import com.saga.inventoryservice.kafka.KafkaTopics.ORDER_CREATED_TOPIC
import com.saga.inventoryservice.kafka.KafkaTopics.ORDER_FAILED_TOPIC
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.math.BigInteger

val safeJsonParser = Json {
    ignoreUnknownKeys = true
}

@Component
class EventListeners(private val inventoryService: InventoryService) {

    private val logger = LoggerFactory.getLogger(EventListeners::class.java)

    @KafkaListener(topics = [ORDER_CREATED_TOPIC], groupId = "order-created-inventory-group")
    fun orderCreatedListener(message: String, ack: Acknowledgment) {
        val orderCreatedEvent = safeJsonParser.decodeFromString<OrderCreatedEvent>(message)
        logger.info("Received message from $ORDER_CREATED_TOPIC topic: $orderCreatedEvent")
        inventoryService.persistInventory(
            orderId = BigInteger(orderCreatedEvent.orderId),
            inventoryCount = orderCreatedEvent.inventoryCount,
            customerName = orderCreatedEvent.customerName,
        )
        ack.acknowledge()
    }


    @KafkaListener(topics = [ORDER_FAILED_TOPIC], groupId = "order-failed-inventory-group")
    fun orderFailedListener(message: String, ack: Acknowledgment) {
        val orderFailedEvent = safeJsonParser.decodeFromString<OrderFailedEvent>(message)
        logger.info("Received message from $ORDER_FAILED_TOPIC topic: $orderFailedEvent")
        inventoryService.revertInventory(
            orderId = BigInteger(orderFailedEvent.orderId),
        )
        ack.acknowledge()
    }
}