package com.varunu28.healthrecordservice.service;

import com.varunu28.healthrecordservice.exception.InsuranceVerificationException;
import com.varunu28.healthrecordservice.model.Plan;
import com.varunu28.healthrecordservice.model.RegisterResponse;
import com.varunu28.healthrecordservice.model.VerificationRequest;
import com.varunu28.healthrecordservice.model.VerificationResponse;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class HealthRecordService {

    private static final Plan FALLBACK_PLAN = new Plan("Fallback Plan", 0.0, 0.0);

    private final RestClient insuranceServiceRestClient;
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final RetryRegistry retryRegistry;

    public HealthRecordService(
        @Qualifier("insuranceServiceRestClient") RestClient insuranceServiceRestClient,
        CircuitBreakerFactory circuitBreakerFactory,
        RetryRegistry retryRegistry) {
        this.insuranceServiceRestClient = insuranceServiceRestClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.retryRegistry = retryRegistry;
    }

    public RegisterResponse register(
        String firstName, String lastName, int age, String controlNumber, String policyNumber) {
        VerificationRequest verificationRequest = new VerificationRequest(controlNumber, policyNumber, LocalDate.now());
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("insuranceService");
        Retry retry = retryRegistry.retry("insuranceServiceRetry");

        return circuitBreaker.run(
            () -> retry.executeSupplier(() ->
                callVerificationService(verificationRequest, firstName, lastName, age, controlNumber, policyNumber)
            ),
            throwable -> getFallbackResponse(firstName, lastName, age, controlNumber, policyNumber, throwable)
        );
    }

    private RegisterResponse callVerificationService(
        VerificationRequest verificationRequest,
        String firstName,
        String lastName,
        int age,
        String controlNumber,
        String policyNumber) {
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
