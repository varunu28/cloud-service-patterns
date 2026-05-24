package com.saga.orderservice

import com.saga.orderservice.kafka.KafkaTopics.ORDER_CREATED_TOPIC
import com.saga.orderservice.kafka.KafkaTopics.ORDER_FAILED_TOPIC
import com.saga.orderservice.kafka.OrderCreatedEvent
import com.saga.orderservice.kafka.OrderFailedEvent
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
    private val orderEventRepository: OrderEventRepository,
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

    @Transactional
    fun processPaymentSuccessEvent(orderId: BigInteger) {
        val orderById = orderRepository.findOrderById(orderId) ?: throw OrderNotFoundException()
        if (orderById.status == "FAILED") {
            return
        }
        val now = LocalDateTime.now()
        val orderEvent = OrderEvent(
            orderId = orderId,
            eventName = "payment_success",
            createdAt = now,
        )
        orderEventRepository.save(orderEvent)
        val allOrderEvents = orderEventRepository.findAllByOrderId(orderId = orderId)
        if (allOrderEvents.size == 2) {
            orderById.updatedAt = now
            orderById.status = "SUCCEEDED"
            orderRepository.save(orderById)
        }
    }

    @Transactional
    fun processPaymentFailedEvent(orderId: BigInteger) {
        val orderById = orderRepository.findOrderById(orderId) ?: throw OrderNotFoundException()
        val now = LocalDateTime.now()
        val orderEvent = OrderEvent(
            orderId = orderId,
            eventName = "payment_failed",
            createdAt = now,
        )
        orderEventRepository.save(orderEvent)
        orderById.status = "FAILED"
        orderRepository.save(orderById)
        val orderFailedEvent = OrderFailedEvent(orderId.toString())
        val eventString = Json.encodeToString(orderFailedEvent)
        kafkaTemplate.send(ORDER_FAILED_TOPIC, eventString)
    }

    @Transactional
    fun processInventorySuccessEvent(orderId: BigInteger) {
        val orderById = orderRepository.findOrderById(orderId) ?: throw OrderNotFoundException()
        if (orderById.status == "FAILED") {
            return
        }
        val now = LocalDateTime.now()
        val orderEvent = OrderEvent(
            orderId = orderId,
            eventName = "inventory_success",
            createdAt = now,
        )
        orderEventRepository.save(orderEvent)
        val allOrderEvents = orderEventRepository.findAllByOrderId(orderId = orderId)
        if (allOrderEvents.size == 2) {
            orderById.updatedAt = now
            orderById.status = "SUCCEEDED"
            orderRepository.save(orderById)
        }
    }

    @Transactional
    fun processInventoryFailedEvent(orderId: BigInteger) {
        val orderById = orderRepository.findOrderById(orderId) ?: throw OrderNotFoundException()
        val now = LocalDateTime.now()
        val orderEvent = OrderEvent(
            orderId = orderId,
            eventName = "inventory_failed",
            createdAt = now,
        )
        orderEventRepository.save(orderEvent)
        orderById.status = "FAILED"
        orderRepository.save(orderById)
        val orderFailedEvent = OrderFailedEvent(orderId.toString())
        val eventString = Json.encodeToString(orderFailedEvent)
        kafkaTemplate.send(ORDER_FAILED_TOPIC, eventString)
    }
}