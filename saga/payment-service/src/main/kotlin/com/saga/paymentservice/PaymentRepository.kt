package com.saga.paymentservice

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.math.BigInteger

@Repository
interface PaymentRepository : CrudRepository<Payment, BigInteger>