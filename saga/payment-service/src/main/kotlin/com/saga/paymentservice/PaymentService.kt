package com.saga.paymentservice

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class PaymentService(
    val paymentRepository: PaymentRepository,
    val paymentTransactionRepository: PaymentTransactionRepository
) {

    @Value($$"${payment.amount.threshold}")
    private var amountThreshold: Double = 10000.0

    @Transactional
    fun persistPayment(
        orderId: BigInteger,
        amount: Double,
        customerName: String,
        idempotencyKey: String
    ) {
        if (amount > amountThreshold) {
            throw PaymentAmountExceededException()
        }
        val now = LocalDateTime.now()
        val payment = Payment(
            customerName = customerName,
            idempotencyKey = idempotencyKey,
            amount = amount,
            status = "CHARGED",
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
    }

    @Transactional
    fun revertPayment(orderId: BigInteger) {
        val paymentByOrderId = paymentRepository.findByOrderId(orderId = orderId) ?: return
        val now = LocalDateTime.now()
        val paymentId = paymentByOrderId.id ?: BigInteger.ZERO
        // Add payment transaction with reverted status & negative amount
        val paymentTransaction = PaymentTransaction(
            paymentId = paymentId,
            transactionType = "REFUND",
            amount = paymentByOrderId.amount * -1,
            createdAt = now
        )
        paymentTransactionRepository.save(paymentTransaction)
        // Revert payment
        paymentByOrderId.status = "REFUNDED"
        paymentByOrderId.amount = 0.0
        paymentByOrderId.updatedAt = now
        paymentRepository.save(paymentByOrderId)
    }
}