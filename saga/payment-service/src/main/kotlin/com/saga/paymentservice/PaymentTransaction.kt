package com.saga.paymentservice

import jakarta.persistence.*
import java.math.BigInteger
import java.time.LocalDateTime

@Entity
@Table(name = "payment_transactions")
data class PaymentTransaction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: BigInteger? = null,
    @Column("payment_id") val paymentId: BigInteger,
    @Column("transaction_type") val transactionType: String,
    val amount: Double,
    @Column("created_at") val createdAt: LocalDateTime
)
