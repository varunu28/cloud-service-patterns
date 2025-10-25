package com.varunu28.paymentservice.advice;

import com.varunu28.paymentservice.exception.DuplicateIdempotencyException;
import com.varunu28.paymentservice.exception.PaymentNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PaymentControllerAdvice {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<String> handlePaymentNotFoundException(PaymentNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateIdempotencyException.class)
    public ResponseEntity<String> handleDuplicateIdempotencyException(DuplicateIdempotencyException ex) {
        return ResponseEntity.status(409).body(ex.getMessage());
    }
}
