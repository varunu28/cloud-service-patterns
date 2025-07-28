package com.varunu28.insuranceservice.service;

import com.varunu28.insuranceservice.exception.BadVerificationRequest;
import com.varunu28.insuranceservice.exception.ControlNumberNotFoundException;
import com.varunu28.insuranceservice.model.VerificationResponse;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VerificationServiceTest {

    private final VerificationService service = new VerificationService();

    @Test
    void verify_successful() {
        LocalDate validDate = LocalDate.now();
        VerificationResponse response = service.verify("12345", "POLICY123", validDate);
        assertEquals("SUCCESS", response.status());
        assertEquals(2, response.eligiblePlans().size());
        assertNull(response.failureReason());
    }

    @Test
    void verify_controlNumberNotFound() {
        LocalDate validDate = LocalDate.now();
        assertThrows(
            ControlNumberNotFoundException.class, () ->
                service.verify("99999", "POLICY123", validDate));
    }

    @Test
    void verify_policyNumberMismatch() {
        LocalDate validDate = LocalDate.of(2025, 2, 1);
        assertThrows(
            BadVerificationRequest.class, () ->
                service.verify("12345", "WRONGPOLICY", validDate));
    }

    @Test
    void verify_requestDateBeforePlanStart() {
        LocalDate beforeStart = LocalDate.of(2024, 12, 31);
        assertThrows(
            BadVerificationRequest.class, () ->
                service.verify("12345", "POLICY123", beforeStart));
    }

    @Test
    void verify_requestDateAfterPlanEnd() {
        LocalDate afterEnd = LocalDate.of(2026, 1, 2);
        assertThrows(
            BadVerificationRequest.class, () ->
                service.verify("12345", "POLICY123", afterEnd));
    }

    @Test
    void verify_emptyControlNumber() {
        LocalDate validDate = LocalDate.of(2025, 2, 1);
        assertThrows(
            BadVerificationRequest.class, () ->
                service.verify("", "POLICY123", validDate));
    }

    @Test
    void verify_nullOrEmptyPolicyNumber() {
        LocalDate validDate = LocalDate.of(2025, 2, 1);
        assertThrows(
            BadVerificationRequest.class, () ->
                service.verify("12345", "", validDate));
    }

    @Test
    void verify_requestDateInPast() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        assertThrows(
            BadVerificationRequest.class, () ->
                service.verify("12345", "POLICY123", pastDate));
    }
}
