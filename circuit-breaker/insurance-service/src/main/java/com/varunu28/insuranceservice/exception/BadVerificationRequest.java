package com.varunu28.insuranceservice.exception;

public class BadVerificationRequest extends RuntimeException {
    public BadVerificationRequest(String message) {
        super(message);
    }
}
