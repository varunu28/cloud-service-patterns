package com.varunu28.healthrecordservice.service;

import com.varunu28.healthrecordservice.exception.InsuranceVerificationException;
import com.varunu28.healthrecordservice.model.Plan;
import com.varunu28.healthrecordservice.model.RegisterResponse;
import com.varunu28.healthrecordservice.model.VerificationRequest;
import com.varunu28.healthrecordservice.model.VerificationResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

@Service
public class HealthRecordService {

    private static final Plan FALLBACK_PLAN = new Plan("Fallback Plan", 0.0, 0.0);

    private final RestClient insuranceServiceRestClient;

    public HealthRecordService(
        @Qualifier("insuranceServiceRestClient") RestClient insuranceServiceRestClient) {
        this.insuranceServiceRestClient = insuranceServiceRestClient;
    }

    public RegisterResponse register(
        String firstName, String lastName, int age, String controlNumber, String policyNumber) {
        VerificationRequest verificationRequest = new VerificationRequest(controlNumber, policyNumber, LocalDate.now());
        return callVerificationService(verificationRequest, firstName, lastName, age, controlNumber, policyNumber);
    }

    private RegisterResponse callVerificationService(
        VerificationRequest verificationRequest,
        String firstName,
        String lastName,
        int age,
        String controlNumber,
        String policyNumber) {
        try {
            VerificationResponse verificationResponse = insuranceServiceRestClient.post()
                .body(verificationRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(VerificationResponse.class);
            return new RegisterResponse(
                firstName,
                lastName,
                age,
                controlNumber,
                policyNumber,
                Objects.requireNonNull(verificationResponse).eligiblePlans()
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new InsuranceVerificationException(
                    e.getStatusCode(),
                    e.getStatusText(),
                    e.getResponseBodyAsByteArray(),
                    null);
            }
            throw e;
        } catch (HttpServerErrorException e) {
            return getFallbackResponse(firstName, lastName, age, controlNumber, policyNumber, e);
        }
    }

    private RegisterResponse getFallbackResponse(
        String firstName,
        String lastName,
        int age,
        String controlNumber,
        String policyNumber,
        Throwable throwable) {
        return new RegisterResponse(
            firstName,
            lastName,
            age,
            controlNumber,
            policyNumber,
            List.of(FALLBACK_PLAN)
        );
    }
}
