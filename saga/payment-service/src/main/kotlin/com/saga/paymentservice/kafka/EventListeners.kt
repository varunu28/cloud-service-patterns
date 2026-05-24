package com.saga.paymentservice.kafka

import com.saga.paymentservice.PaymentService
import com.saga.paymentservice.kafka.KafkaTopics.ORDER_CREATED_TOPIC
import com.saga.paymentservice.kafka.KafkaTopics.ORDER_FAILED_TOPIC
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
class EventListeners(val paymentService: PaymentService) {

    private val logger = LoggerFactory.getLogger(EventListeners::class.java)

    @KafkaListener(topics = [ORDER_CREATED_TOPIC], groupId = "order-created-payment-group")
    fun orderCreatedListener(message: String, ack: Acknowledgment) {
        val orderCreatedEvent = safeJsonParser.decodeFromString<OrderCreatedEvent>(message)
        logger.info("Received message from ${ORDER_CREATED_TOPIC}: $orderCreatedEvent")
        paymentService.persistPayment(
            orderId = BigInteger(orderCreatedEvent.orderId),
            amount = orderCreatedEvent.amount,
            customerName = orderCreatedEvent.customerName,
            idempotencyKey = orderCreatedEvent.idempotencyKey
        )
        ack.acknowledge()
    }

    @KafkaListener(topics = [ORDER_FAILED_TOPIC], groupId = "order-failed-payment-group")
    fun orderFailedListener(message: String, ack: Acknowledgment) {
        val orderFailedEvent = safeJsonParser.decodeFromString<OrderFailedEvent>(message)
        logger.info("Received message from ${ORDER_FAILED_TOPIC}: $orderFailedEvent")
        paymentService.revertPayment(
            orderId = BigInteger(orderFailedEvent.orderId)
        )
        ack.acknowledge()
    }
}