package com.varunu28.insuranceservice.controller;

import com.varunu28.insuranceservice.model.VerificationRequest;
import com.varunu28.insuranceservice.model.VerificationResponse;
import com.varunu28.insuranceservice.service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/verify")
public class RecordVerificationController {

    private final VerificationService verificationService;

    public RecordVerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<VerificationResponse> verifyRecords(@Valid @RequestBody VerificationRequest request) {
        return ResponseEntity.ok(verificationService.verify(
            request.controlNumber(),
            request.policyNumber(),
            request.requestDate()));
    }
}
