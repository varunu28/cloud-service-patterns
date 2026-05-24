package com.saga.orderservice.kafka

import com.saga.orderservice.OrderService
import com.saga.orderservice.kafka.KafkaTopics.INVENTORY_RESERVATION_FAILED_TOPIC
import com.saga.orderservice.kafka.KafkaTopics.INVENTORY_RESERVATION_SUCCESSFUL_TOPIC
import com.saga.orderservice.kafka.KafkaTopics.PAYMENT_FAILED_TOPIC
import com.saga.orderservice.kafka.KafkaTopics.PAYMENT_SUCCEEDED_TOPIC
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

    @KafkaListener(topics = [PAYMENT_SUCCEEDED_TOPIC], groupId = "payment-succeeded-order-group")
    fun paymentSucceededListener(message: String, ack: Acknowledgment) {
        val paymentEvent = safeJsonParser.decodeFromString<PaymentEvent>(message)
        logger.info("Received message from ${PAYMENT_SUCCEEDED_TOPIC}: $paymentEvent")
        orderService.processPaymentSuccessEvent(BigInteger(paymentEvent.orderId))
        ack.acknowledge()
    }

    @KafkaListener(topics = [PAYMENT_FAILED_TOPIC], groupId = "payment-failed-order-group")
    fun paymentFailedListener(message: String, ack: Acknowledgment) {
        val paymentEvent = safeJsonParser.decodeFromString<PaymentEvent>(message)
        logger.info("Received message from ${PAYMENT_FAILED_TOPIC}: $paymentEvent")
        orderService.processPaymentFailedEvent(BigInteger(paymentEvent.orderId))
        ack.acknowledge()
    }

    @KafkaListener(topics = [INVENTORY_RESERVATION_SUCCESSFUL_TOPIC], groupId = "inventory-succeeded-order-group")
    fun inventorySucceededListener(message: String, ack: Acknowledgment) {
        val inventoryEvent = safeJsonParser.decodeFromString<InventoryEvent>(message)
        logger.info("Received message from ${INVENTORY_RESERVATION_SUCCESSFUL_TOPIC}: $inventoryEvent")
        orderService.processInventorySuccessEvent(BigInteger(inventoryEvent.orderId))
        ack.acknowledge()
    }

    @KafkaListener(topics = [INVENTORY_RESERVATION_FAILED_TOPIC], groupId = "inventory-failed-order-group")
    fun inventoryFailedListener(message: String, ack: Acknowledgment) {
        val inventoryEvent = safeJsonParser.decodeFromString<InventoryEvent>(message)
        logger.info("Received message from ${INVENTORY_RESERVATION_FAILED_TOPIC}: $inventoryEvent")
        orderService.processInventoryFailedEvent(BigInteger(inventoryEvent.orderId))
        ack.acknowledge()
    }
}