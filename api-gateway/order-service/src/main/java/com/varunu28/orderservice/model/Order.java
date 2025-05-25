package com.varunu28.orderservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.varunu28.orderservice.exception.InvalidUpdateException;
import java.time.ZonedDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {

    private UUID id;

    @JsonProperty("description")
    private String description;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;

    @JsonProperty("deleted_at")
    private ZonedDateTime deletedAt;

    public Order(UUID id, String description, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setDescription(String newDescription, String olderDescription) {
        if (!olderDescription.equals(description)) {
            throw new InvalidUpdateException();
        }
        description = newDescription;
        updatedAt = ZonedDateTime.now();
    }

    public void markAsDeleted() {
        ZonedDateTime now = ZonedDateTime.now();
        updatedAt = now;
        deletedAt = now;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
