package com.saga.paymentservice

import jakarta.persistence.*
import java.math.BigInteger
import java.time.LocalDateTime

@Entity
@Table(name = "payments")
data class Payment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: BigInteger? = null,
    @Column("customer_name") val customerName: String,
    @Column("order_id") var orderId: BigInteger,
    var amount: Double,
    var status: String,
    @Column("idempotency_key") val idempotencyKey: String,
    @Column("created_at") val createdAt: LocalDateTime,
    @Column("updated_at") var updatedAt: LocalDateTime
)