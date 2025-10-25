package com.varunu28.paymentservice.exception;

public class DuplicateIdempotencyException extends Exception {
    public DuplicateIdempotencyException() {
        super("A payment with the given idempotency key already exists.");
    }
}
