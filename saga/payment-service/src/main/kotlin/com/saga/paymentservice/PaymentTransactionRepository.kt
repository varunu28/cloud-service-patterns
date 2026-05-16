package com.saga.paymentservice

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.math.BigInteger

@Repository
interface PaymentTransactionRepository : CrudRepository<PaymentTransaction, BigInteger>