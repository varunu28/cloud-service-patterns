package com.saga.orderservice.saga

import com.saga.orderservice.kafka.KafkaTopics.ORDER_FAILED_TOPIC
import com.saga.orderservice.kafka.KafkaTopics.ORDER_SUCCESSFUL_TOPIC
import com.saga.orderservice.kafka.OrderFailedEvent
import com.saga.orderservice.kafka.OrderSucceededEvent
import dev.dbos.transact.workflow.Workflow
import kotlinx.serialization.json.Json
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.util.concurrent.CompletableFuture

@Service
class OrderWorkflowService(
    private val orderStepService: OrderStepService,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    @Workflow
    fun processOrder(
        orderId: BigInteger,
        inventoryCount: Int,
        amount: Double,
        customerName: String,
        idempotencyKey: String
    ) {
        val paymentFuture = CompletableFuture.supplyAsync {
            orderStepService.chargePayment(
                orderId = orderId,
                idempotencyKey = idempotencyKey,
                amount = amount,
                customerName = customerName,
            )
        }
        val inventoryFuture = CompletableFuture.supplyAsync {
            orderStepService.reserveInventory(
                orderId = orderId,
                customerName = customerName,
                inventoryCount = inventoryCount,
            )
        }

        // Flags for tracking request states
        var paymentCharged = false
        var inventoryReserved = false

        try {
            CompletableFuture.allOf(paymentFuture, inventoryFuture).join()

            paymentCharged = paymentFuture.get()
            inventoryReserved = inventoryFuture.get()

            if (paymentCharged && inventoryReserved) {
                val orderSucceededEvent = OrderSucceededEvent(orderId = orderId.toString())
                val eventString = Json.encodeToString(orderSucceededEvent)
                kafkaTemplate.send(ORDER_SUCCESSFUL_TOPIC, eventString)
            } else {
                throw RuntimeException("One or more services failed to complete processing.")
            }
        } catch (_: Exception) {
            val paymentWasSuccess = try {
                paymentFuture.isDone && !paymentFuture.isCompletedExceptionally && paymentFuture.get()
            } catch (_: Exception) {
                false
            }
            val inventoryWasSuccess = try {
                inventoryFuture.isDone && !inventoryFuture.isCompletedExceptionally && inventoryFuture.get()
            } catch (_: Exception) {
                false
            }

            if (paymentWasSuccess) {
                orderStepService.reversePayment(orderId)
            }
            if (inventoryWasSuccess) {
                orderStepService.revertInventoryReservation(orderId)
            }

            val orderFailedEvent = OrderFailedEvent(orderId = orderId.toString())
            val eventString = Json.encodeToString(orderFailedEvent)
            kafkaTemplate.send(ORDER_FAILED_TOPIC, eventString)
        }
    }
}