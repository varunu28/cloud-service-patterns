package com.saga.paymentservice

import com.saga.paymentservice.kafka.KafkaTopics.PAYMENT_FAILED_TOPIC
import com.saga.paymentservice.kafka.KafkaTopics.PAYMENT_SUCCEEDED_TOPIC
import com.saga.paymentservice.kafka.PaymentEvent
import jakarta.transaction.Transactional
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class PaymentService(
    val paymentRepository: PaymentRepository,
    val paymentTransactionRepository: PaymentTransactionRepository,
    val kafkaTemplate: KafkaTemplate<String, String>
) {

    @Value("\${payment.amount.threshold}")
    private var amountThreshold: Double = 10000.0

    @Transactional
    fun persistPayment(
        orderId: BigInteger,
        amount: Double,
        customerName: String,
        idempotencyKey: String
    ) {
        if (amount > amountThreshold) {
            val paymentSuccessEvent = PaymentEvent(orderId = orderId.toString())
            val eventString = Json.encodeToString(paymentSuccessEvent)
            kafkaTemplate.send(PAYMENT_FAILED_TOPIC, eventString)
            return
        }
        val now = LocalDateTime.now()
        val payment = Payment(
            customerName = customerName,
            idempotencyKey = idempotencyKey,
            amount = amount,
            status = "CREATED",
            orderId = orderId,
            createdAt = now,
            updatedAt = now,
        )
        val savedPayment: Payment
        try {
            savedPayment = paymentRepository.save(payment)
        } catch (_: DataIntegrityViolationException) {
            return
        }
        val paymentId = savedPayment.id ?: BigInteger.ZERO
        val paymentTransaction = PaymentTransaction(
            paymentId = paymentId,
            transactionType = "CHARGE",
            amount = amount,
            createdAt = now
        )
        paymentTransactionRepository.save(paymentTransaction)
        val paymentSuccessEvent = PaymentEvent(orderId = orderId.toString())
        val eventString = Json.encodeToString(paymentSuccessEvent)
        kafkaTemplate.send(PAYMENT_SUCCEEDED_TOPIC, eventString)
    }
}