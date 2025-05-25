package com.varunu28.orderservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateOrderRequest(
    @JsonProperty("previous_description") String previousDescription,
    @JsonProperty("new_description") String updatedDescription) {
}
