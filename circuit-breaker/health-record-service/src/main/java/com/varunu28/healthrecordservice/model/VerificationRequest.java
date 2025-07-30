package com.varunu28.healthrecordservice.model;

import java.time.LocalDate;

public record VerificationRequest(String controlNumber,
                                  String policyNumber,
                                  LocalDate requestDate) {
}