package com.varunu28.healthrecordservice.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
    @NotNull String firstName,
    @NotNull String lastName,
    @NotNull
    @Min(value = 18, message = "patient's age should be greater than or equal to 18")
    @Max(value = 100, message = "patient's age should be less than or equal to 100") int age,
    @NotNull String controlNumber,
    @NotNull String policyNumber) {}
