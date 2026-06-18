package com.varunu28.centralservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.resilience.InvocationRejectedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ResilienceExceptionHandler {

    @ExceptionHandler(InvocationRejectedException.class)
    public ResponseEntity<String> handleInvocationRejectedException(InvocationRejectedException ignored) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Bulkhead limit reached. Try again later.");
    }
}

