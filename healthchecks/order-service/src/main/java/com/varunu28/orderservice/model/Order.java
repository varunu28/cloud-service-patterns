package com.varunu28.orderservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private String description;

    private double amount;

    private UUID customerId;

    private UUID paymentId;

    private String idempotencyKey;

    private Date createdAt;

    private Date updatedAt;

    public Order() {}

    public Order(String description, double amount, UUID customerId, UUID paymentId, String idempotencyKey) {
        this.description = description;
        this.amount = amount;
        this.customerId = customerId;
        this.paymentId = paymentId;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public UUID getCustomerId() {
        return customerId;
    }
}
