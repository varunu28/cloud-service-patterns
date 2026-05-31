package com.saga.paymentservice

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.math.BigInteger

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(val paymentService: PaymentService) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun chargePayment(@RequestBody request: CreatePaymentDto) {
        paymentService.persistPayment(
            orderId = BigInteger(request.orderId),
            amount = request.amount,
            idempotencyKey = request.idempotencyKey,
            customerName = request.customerName,
        )
    }

    @PostMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    fun revertPayment(@PathVariable orderId: BigInteger) {
        paymentService.revertPayment(orderId = orderId)
    }
}