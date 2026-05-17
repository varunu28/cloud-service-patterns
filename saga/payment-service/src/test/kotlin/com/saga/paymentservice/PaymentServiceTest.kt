package com.saga.paymentservice

import com.saga.paymentservice.kafka.KafkaTopics.PAYMENT_FAILED_TOPIC
import com.saga.paymentservice.kafka.KafkaTopics.PAYMENT_SUCCEEDED_TOPIC
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.kafka.core.KafkaTemplate
import java.math.BigInteger
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class PaymentServiceTest {

    @Mock
    private lateinit var paymentRepository: PaymentRepository

    @Mock
    private lateinit var paymentTransactionRepository: PaymentTransactionRepository

    @Mock
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @InjectMocks
    private lateinit var paymentService: PaymentService

    @Test
    fun `should publish kafka event for successful processing`() {
        // Arrange
        val idempotencyKey = UUID.randomUUID().toString()
        val amount = 100.0
        val orderId = BigInteger("123")
        val savedPayment = Mockito.mock(Payment::class.java)
        `when`(paymentRepository.save(any())).thenReturn(savedPayment)
        val paymentId = BigInteger("456")
        `when`(savedPayment.id).thenReturn(paymentId)
        val savedPaymentTransaction = Mockito.mock(PaymentTransaction::class.java)
        `when`(paymentTransactionRepository.save(any())).thenReturn(savedPaymentTransaction)

        // Act
        paymentService.persistPayment(
            orderId = orderId,
            amount = amount,
            idempotencyKey = idempotencyKey,
            customerName = "customer",
        )

        // Assert
        verify(paymentRepository).save(org.mockito.kotlin.check { payment ->
            assertEquals("customer", payment.customerName)
            assertEquals(amount, payment.amount)
            assertEquals(orderId, payment.orderId)
            assertEquals(idempotencyKey, payment.idempotencyKey)

        })
        verify(paymentTransactionRepository).save(org.mockito.kotlin.check { paymentTxn ->
            assertEquals(amount, paymentTxn.amount)
            assertEquals(paymentId, paymentTxn.paymentId)
            assertEquals("CHARGE", paymentTxn.transactionType)
        })
        verify(kafkaTemplate).send(eq(PAYMENT_SUCCEEDED_TOPIC), org.mockito.kotlin.check { jsonPayload ->
            assertTrue { jsonPayload.contains("""orderId":"$orderId"""") }
        })
    }

    @Test
    fun `should publish kafka event for failed processing`() {
        // Arrange
        val idempotencyKey = UUID.randomUUID().toString()
        val amount = 11000.0 // Amount exceeding threshold
        val orderId = BigInteger("123")

        // Act
        paymentService.persistPayment(
            orderId = orderId,
            amount = amount,
            idempotencyKey = idempotencyKey,
            customerName = "customer",
        )

        // Assert
        verify(paymentRepository, never()).save(any())
        verify(paymentTransactionRepository, never()).save(any())
        verify(kafkaTemplate, never()).send(eq(PAYMENT_SUCCEEDED_TOPIC), any(String::class.java))
        verify(kafkaTemplate).send(eq(PAYMENT_FAILED_TOPIC), org.mockito.kotlin.check { jsonPayload ->
            assertTrue { jsonPayload.contains("""orderId":"$orderId"""") }
        })
    }

    @Test
    fun `should not publish kafka event for duplicate processing`() {
        // Arrange
        val idempotencyKey = UUID.randomUUID().toString()
        val amount = 100.0
        val orderId = BigInteger("123")
        `when`(paymentRepository.save(any())).thenThrow(DataIntegrityViolationException::class.java)

        // Act
        paymentService.persistPayment(
            orderId = orderId,
            amount = amount,
            idempotencyKey = idempotencyKey,
            customerName = "customer",
        )

        // Assert
        verify(paymentRepository).save(any())
        verify(paymentTransactionRepository, never()).save(any())
        verify(kafkaTemplate, never()).send(any(String::class.java), any(String::class.java))
    }
}