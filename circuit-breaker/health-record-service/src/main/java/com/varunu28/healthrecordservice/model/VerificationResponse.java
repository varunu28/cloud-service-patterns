package com.varunu28.healthrecordservice.model;

import java.util.List;

public record VerificationResponse(String status, List<Plan> eligiblePlans, String failureReason) {}

