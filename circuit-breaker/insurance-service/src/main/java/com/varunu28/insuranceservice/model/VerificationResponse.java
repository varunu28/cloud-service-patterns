package com.varunu28.insuranceservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VerificationResponse(String status, List<Plan> eligiblePlans, String failureReason) {
}
