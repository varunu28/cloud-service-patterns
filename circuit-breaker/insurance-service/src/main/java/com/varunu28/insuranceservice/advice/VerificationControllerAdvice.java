package com.varunu28.insuranceservice.advice;

import com.varunu28.insuranceservice.exception.BadVerificationRequest;
import com.varunu28.insuranceservice.exception.ControlNumberNotFoundException;
import com.varunu28.insuranceservice.model.VerificationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class VerificationControllerAdvice {

    @ExceptionHandler(BadVerificationRequest.class)
    public ResponseEntity<VerificationResponse> handleBadVerificationRequest(BadVerificationRequest ex) {
        VerificationResponse response = new VerificationResponse("FAILURE", null, ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ControlNumberNotFoundException.class)
    public ResponseEntity<VerificationResponse> handleControlNumberNotFound(ControlNumberNotFoundException ex) {
        VerificationResponse response = new VerificationResponse("FAILURE", null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
