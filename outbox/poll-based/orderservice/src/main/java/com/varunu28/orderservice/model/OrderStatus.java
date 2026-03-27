package com.varunu28.orderservice.model;

public enum OrderStatus {
    CREATED("CREATED"),
    CANCELLED("CANCELLED"),
    PROCESSED("PROCESSED");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
