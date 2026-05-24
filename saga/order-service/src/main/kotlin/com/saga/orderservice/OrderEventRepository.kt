package com.saga.orderservice

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.math.BigInteger

@Repository
interface OrderEventRepository : CrudRepository<OrderEvent, BigInteger> {

    fun findAllByOrderId(orderId: BigInteger): List<OrderEvent>
}