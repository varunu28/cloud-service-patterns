package com.saga.paymentservice

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.math.BigInteger

@Repository
interface PaymentRepository : CrudRepository<Payment, BigInteger> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByOrderId(orderId: BigInteger): Payment?
}