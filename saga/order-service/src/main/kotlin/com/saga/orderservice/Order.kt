package com.saga.orderservice

import jakarta.persistence.*
import java.math.BigInteger
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: BigInteger? = null,
    @Column(name = "customer_name") val customerName: String,
    var amount: Double,
    var status: String,
    @Column(name = "inventory_count") val inventoryCount: Int,
    @Column(name = "created_at") val createdAt: LocalDateTime,
    @Column(name = "updated_at") var updatedAt: LocalDateTime,
)
