package com.varunu28.orderpickupservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pickup {

    private UUID id;

    @JsonProperty("description")
    private String description;

    @JsonProperty("assignee_id")
    private UUID assigneeId;

    @JsonProperty("order_id")
    private UUID orderId;

    @JsonProperty("completed")
    private boolean isComplete;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;

    @JsonProperty("cancelled_at")
    private ZonedDateTime cancelledAt;

    public Pickup(
        UUID id,
        String description,
        UUID assigneeId,
        UUID orderId,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt) {
        this.id = id;
        this.description = description;
        this.assigneeId = assigneeId;
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void markAsCompleted() {
        isComplete = true;
        updatedAt = ZonedDateTime.now();
    }

    public void markAsCancelled() {
        cancelledAt = ZonedDateTime.now();
    }
}
