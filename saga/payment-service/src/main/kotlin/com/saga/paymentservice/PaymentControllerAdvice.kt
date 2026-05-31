package com.saga.paymentservice

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class PaymentControllerAdvice {

    @ExceptionHandler(PaymentAmountExceededException::class)
    fun handlePaymentAmountExceedException(e: PaymentAmountExceededException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
    }
}