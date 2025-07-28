package com.varunu28.insuranceservice.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record VerificationRequest(@NotNull String controlNumber,
                                  @NotNull String policyNumber,
                                  @NotNull LocalDate requestDate) {
}
