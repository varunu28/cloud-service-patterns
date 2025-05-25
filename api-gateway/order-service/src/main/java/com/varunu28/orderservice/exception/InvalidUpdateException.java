package com.varunu28.orderservice.exception;

public class InvalidUpdateException extends RuntimeException {

    public InvalidUpdateException() {
        super("Invalid order update. Previous description doesn't matches the order description");
    }
}
