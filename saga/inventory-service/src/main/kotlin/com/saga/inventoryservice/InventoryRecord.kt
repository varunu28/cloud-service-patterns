package com.saga.inventoryservice

import jakarta.persistence.*
import java.math.BigInteger
import java.time.LocalDateTime

@Entity
@Table(name = "inventory_record")
data class InventoryRecord(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: BigInteger? = null,
    @Column("inventory_id") val inventoryId: BigInteger,
    @Column("record_type") val recordType: String,
    @Column("inventory_count") val inventoryCount: Int,
    @Column("created_at") val createdAt: LocalDateTime
)
