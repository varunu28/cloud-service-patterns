package com.saga.orderservice.kafka

import com.saga.orderservice.OrderService
import com.saga.orderservice.kafka.KafkaTopics.ORDER_CREATED_TOPIC
import com.saga.orderservice.kafka.KafkaTopics.ORDER_FAILED_TOPIC
import com.saga.orderservice.kafka.KafkaTopics.ORDER_SUCCESSFUL_TOPIC
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
class EventListeners(val orderService: OrderService) {

    private val logger = LoggerFactory.getLogger(EventListeners::class.java)

    @KafkaListener(topics = [ORDER_CREATED_TOPIC], groupId = "order-created-order-group")
    fun orderCreatedEventListener(message: String, ack: Acknowledgment) {
        val orderEvent = safeJsonParser.decodeFromString<OrderCreatedEvent>(message)
        logger.info("Received message from ${ORDER_CREATED_TOPIC}: $orderEvent")
        orderService.processOrderCreatedEvent(
            orderId = BigInteger(orderEvent.orderId),
            customerName = orderEvent.customerName,
            inventoryCount = orderEvent.inventoryCount,
            amount = orderEvent.amount,
            idempotencyKey = orderEvent.idempotencyKey,
        )
        ack.acknowledge()
    }

    @KafkaListener(topics = [ORDER_FAILED_TOPIC], groupId = "order-failed-order-group")
    fun orderFailedEventListener(message: String, ack: Acknowledgment) {
        val orderEvent = safeJsonParser.decodeFromString<OrderFailedEvent>(message)
        logger.info("Received message from ${ORDER_FAILED_TOPIC}: $orderEvent")
        orderService.processOrderFailedEvent(BigInteger(orderEvent.orderId))
        ack.acknowledge()
    }

    @KafkaListener(topics = [ORDER_SUCCESSFUL_TOPIC], groupId = "order-succeeded-order-group")
    fun orderSucceededEventListener(message: String, ack: Acknowledgment) {
        val orderEvent = safeJsonParser.decodeFromString<OrderSucceededEvent>(message)
        logger.info("Received message from ${ORDER_SUCCESSFUL_TOPIC}: $orderEvent")
        orderService.processOrderSucceededEvent(BigInteger(orderEvent.orderId))
        ack.acknowledge()
    }
}