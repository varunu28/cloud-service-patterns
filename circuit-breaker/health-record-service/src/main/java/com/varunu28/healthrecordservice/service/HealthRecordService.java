package com.varunu28.healthrecordservice.service;

import com.varunu28.healthrecordservice.exception.InsuranceVerificationException;
import com.varunu28.healthrecordservice.model.RegisterResponse;
import com.varunu28.healthrecordservice.model.VerificationRequest;
import com.varunu28.healthrecordservice.model.VerificationResponse;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class HealthRecordService {

    private final RestClient insuranceServiceRestClient;

    public HealthRecordService(@Qualifier("insuranceServiceRestClient") RestClient insuranceServiceRestClient) {
        this.insuranceServiceRestClient = insuranceServiceRestClient;
    }

    public RegisterResponse register(
        String firstName, String lastName, int age, String controlNumber, String policyNumber) {
        VerificationRequest verificationRequest = new VerificationRequest(controlNumber, policyNumber, LocalDate.now());
        VerificationResponse verificationResponse = insuranceServiceRestClient.post()
            .body(verificationRequest)
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(VerificationResponse.class);
        if (Objects.requireNonNull(verificationResponse).failureReason() != null) {
            throw new InsuranceVerificationException(
                "Insurance verification failed: " + verificationResponse.failureReason());
        }
        return new RegisterResponse(
            firstName,
            lastName,
            age,
            controlNumber,
            policyNumber,
            verificationResponse.eligiblePlans()
        );
    }
}
