package com.varunu28.orderservice.model;

public enum OutboxEventType {
    ORDER_CREATED("order_created");

    private final String value;

    OutboxEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
