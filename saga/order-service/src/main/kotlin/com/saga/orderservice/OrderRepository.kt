package com.saga.orderservice

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigInteger

@Repository
interface OrderRepository : JpaRepository<Order, BigInteger>