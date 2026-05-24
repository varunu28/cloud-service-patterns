package com.saga.inventoryservice

import jakarta.persistence.*
import java.math.BigInteger
import java.time.LocalDateTime

@Entity
@Table(name = "inventories")
data class Inventory(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: BigInteger? = null,
    @Column("order_id") val orderId: BigInteger,
    @Column("customer_name") val customerName: String,
    @Column("inventory_count") var inventoryCount: Int,
    var status: String,
    @Column("created_at") val createdAt: LocalDateTime,
    @Column("updated_at") var updatedAt: LocalDateTime,
)