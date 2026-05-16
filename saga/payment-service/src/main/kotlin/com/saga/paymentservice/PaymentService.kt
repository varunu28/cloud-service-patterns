package com.saga.paymentservice

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class PaymentService(
    val paymentRepository: PaymentRepository,
    val paymentTransactionRepository: PaymentTransactionRepository
) {

    @Transactional
    fun persistPayment(
        orderId: BigInteger,
        amount: Double,
        customerName: String,
        idempotencyKey: String
    ) {
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
        val savedPayment = paymentRepository.save(payment)
        val paymentId = savedPayment.id ?: BigInteger.ZERO
        val paymentTransaction = PaymentTransaction(
            paymentId = paymentId,
            transactionType = "CHARGE",
            amount = amount,
            createdAt = now
        )
        paymentTransactionRepository.save(paymentTransaction)
        // TODO: Publish payment success message to Kafka
    }
}