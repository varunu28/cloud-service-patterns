package com.varunu28.paymentservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private String idempotencyKey;
    private double amount;
    private UUID customerId;
    private Date createdAt;
    private Date updatedAt;

    public Payment() {
    }

    public Payment(String idempotencyKey, double amount, UUID customerId) {
        this.idempotencyKey = idempotencyKey;
        this.amount = amount;
        this.customerId = customerId;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public UUID getId() {
        return id;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public double getAmount() {
        return amount;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
