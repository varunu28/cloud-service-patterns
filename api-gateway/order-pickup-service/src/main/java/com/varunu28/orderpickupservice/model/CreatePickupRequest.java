package com.varunu28.orderpickupservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record CreatePickupRequest(
    @JsonProperty("assignee_id") UUID assigneeId, String description, @JsonProperty("order_id") UUID orderId) {
}
