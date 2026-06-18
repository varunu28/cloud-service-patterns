package com.varunu28.centralservice;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ResilienceExceptionHandler {

    /**
     * @param ignored {@link ConnectionRequestTimeoutException} is thrown by {@link CloseableHttpClient} when a request
     *                is unable to get a connection from HTTP pool
     * @return ResponseEntity with status code as 429
     */
    @ExceptionHandler(ConnectionRequestTimeoutException.class)
    public ResponseEntity<String> handleConnectionRequestTimeoutException(ConnectionRequestTimeoutException ignored) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Bulkhead limit reached. Try again later.");
    }
}

