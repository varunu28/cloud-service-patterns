package com.saga.orderservice

import com.saga.orderservice.kafka.KafkaTopics.ORDER_CREATED_TOPIC
import com.saga.orderservice.kafka.OrderCreatedEvent
import com.saga.orderservice.saga.OrderWorkflowService
import jakarta.transaction.Transactional
import kotlinx.serialization.json.Json
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.*

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderWorkflowService: OrderWorkflowService,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    @Transactional
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
        val savedOrder = orderRepository.save(order)
        val orderId = savedOrder.id ?: BigInteger.ZERO
        val idempotencyKey = UUID.randomUUID().toString()
        val orderCreatedEvent = OrderCreatedEvent(
            orderId.toString(), savedOrder.inventoryCount, savedOrder.amount, savedOrder.customerName, idempotencyKey
        )
        val eventString = Json.encodeToString(orderCreatedEvent)
        kafkaTemplate.send(ORDER_CREATED_TOPIC, eventString)
        return orderId
    }

    fun getOrderById(id: BigInteger): Order {
        return orderRepository.findByIdOrNull(id) ?: throw OrderNotFoundException()
    }

    fun processOrderCreatedEvent(
        orderId: BigInteger,
        inventoryCount: Int,
        amount: Double,
        customerName: String,
        idempotencyKey: String
    ) {
        orderWorkflowService.processOrder(
            orderId = orderId,
            inventoryCount = inventoryCount,
            customerName = customerName,
            amount = amount,
            idempotencyKey = idempotencyKey
        )
    }

    @Transactional
    fun processOrderFailedEvent(orderId: BigInteger) {
        val order = orderRepository.findByIdOrNull(orderId) ?: return
        order.status = "FAILED"
        order.updatedAt = LocalDateTime.now()
        orderRepository.save(order)
    }

    @Transactional
    fun processOrderSucceededEvent(orderId: BigInteger) {
        val order = orderRepository.findByIdOrNull(orderId) ?: return
        order.status = "SUCCEEDED"
        order.updatedAt = LocalDateTime.now()
        orderRepository.save(order)
    }
}