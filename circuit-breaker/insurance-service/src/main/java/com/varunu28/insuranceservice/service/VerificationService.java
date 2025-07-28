package com.varunu28.insuranceservice.service;

import com.varunu28.insuranceservice.exception.BadVerificationRequest;
import com.varunu28.insuranceservice.exception.ControlNumberNotFoundException;
import com.varunu28.insuranceservice.model.Plan;
import com.varunu28.insuranceservice.model.VerificationResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    private static final Map<String, ProviderRecord> VALID_RECORDS = Map.of(
        "12345", new ProviderRecord(
            "POLICY123",
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2026, 1, 1),
            List.of(
                new Plan("Primary Care", 80.0, 20.0),
                new Plan("Specialist Care", 70.0, 30.0))),
        "67890", new ProviderRecord(
            "POLICY456",
            LocalDate.of(2024, 6, 1),
            LocalDate.of(2025, 6, 1),
            List.of(
                new Plan("Primary Care", 70.0, 35.0),
                new Plan("Specialist Care", 70.0, 30.0),
                new Plan("Emergency Care", 70.0, 50.0)))
    );

    public VerificationResponse verify(String controlNumber, String policyNumber, LocalDate requestDate) {
        if (requestDate.isBefore(LocalDate.now())) {
            throw new BadVerificationRequest("Request date cannot be in the future");
        }
        if (controlNumber.isEmpty()) {
            throw new BadVerificationRequest("Control number cannot be empty");
        }
        if (policyNumber.isEmpty()) {
            throw new BadVerificationRequest("Policy number cannot be empty");
        }
        if (!VALID_RECORDS.containsKey(controlNumber)) {
            throw new ControlNumberNotFoundException("Control number not found: " + controlNumber);
        }
        ProviderRecord record = VALID_RECORDS.get(controlNumber);
        if (!record.policyNumber().equals(policyNumber)) {
            throw new BadVerificationRequest("Policy number does not match");
        }
        if (requestDate.isBefore(record.planStartDate()) || requestDate.isAfter(record.planEndDate())) {
            throw new BadVerificationRequest("Request date is outside the valid plan period");
        }
        return new VerificationResponse("SUCCESS", List.copyOf(record.eligiblePlans()), null);
    }

    record ProviderRecord(String policyNumber, LocalDate planStartDate, LocalDate planEndDate,
                          List<Plan> eligiblePlans) {}
}
