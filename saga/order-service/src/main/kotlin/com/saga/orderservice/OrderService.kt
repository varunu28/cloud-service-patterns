package com.saga.orderservice

import com.saga.orderservice.kafka.KafkaTopics.ORDER_CREATED_TOPIC
import com.saga.orderservice.kafka.OrderCreatedEvent
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.UUID

@Service
class OrderService(
    private val db: OrderRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun createOrder(request: CreateOrderRequest): BigInteger {
        val now = LocalDateTime.now()
        val order = Order(
            customerName = request.customerName,
            amount = request.amount,
            inventoryCount = request.inventoryCount,
            status = "CREATED",
            createdAt = now,
            updatedAt = now
        )
        val savedOrder = db.save(order)
        val orderId = savedOrder.id ?: BigInteger.ZERO
        val idempotencyKey = UUID.randomUUID().toString()
        val orderCreatedEvent = OrderCreatedEvent(
            orderId, savedOrder.inventoryCount, savedOrder.amount, savedOrder.customerName, idempotencyKey)
        kafkaTemplate.send(ORDER_CREATED_TOPIC, orderCreatedEvent.toString())
        return orderId
    }

    fun getOrderById(id: BigInteger): Order {
        return db.findByIdOrNull(id) ?: throw OrderNotFoundException()
    }
}