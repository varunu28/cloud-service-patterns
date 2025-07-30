package com.varunu28.healthrecordservice.model;

import java.util.List;

public record RegisterResponse(String firstName,
                               String lastName,
                               int age,
                               String controlNumber,
                               String policyNumber,
                               List<Plan> eligiblePlans) {}
