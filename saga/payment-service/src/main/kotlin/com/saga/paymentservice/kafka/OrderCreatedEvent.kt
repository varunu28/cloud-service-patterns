package com.saga.paymentservice.kafka

import java.math.BigInteger

data class OrderCreatedEvent(val orderId: BigInteger, val inventoryCount: Int, val amount: Double)
