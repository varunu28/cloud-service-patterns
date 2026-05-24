package com.saga.orderservice

import jakarta.persistence.*
import java.math.BigInteger
import java.time.LocalDateTime

@Entity
@Table(name = "order_events")
data class OrderEvent(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: BigInteger? = null,
    @Column("order_id") val orderId: BigInteger,
    @Column("event_name") val eventName: String,
    @Column("created_at") val createdAt: LocalDateTime,
)
