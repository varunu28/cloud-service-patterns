package com.varunu28.healthrecordservice.controller;

import com.varunu28.healthrecordservice.model.RegisterRequest;
import com.varunu28.healthrecordservice.model.RegisterResponse;
import com.varunu28.healthrecordservice.service.HealthRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/register")
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    public HealthRecordController(HealthRecordService healthRecordService) {
        this.healthRecordService = healthRecordService;
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return ResponseEntity.ok(healthRecordService.register(
            registerRequest.firstName(),
            registerRequest.lastName(),
            registerRequest.age(),
            registerRequest.controlNumber(),
            registerRequest.policyNumber()
        ));
    }
}
