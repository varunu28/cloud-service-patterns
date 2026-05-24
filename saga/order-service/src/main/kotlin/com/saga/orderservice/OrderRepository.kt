package com.saga.orderservice

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Repository
import java.math.BigInteger

@Repository
interface OrderRepository : JpaRepository<Order, BigInteger> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findOrderById(id: BigInteger): Order?
}