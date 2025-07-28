package com.varunu28.insuranceservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varunu28.insuranceservice.exception.BadVerificationRequest;
import com.varunu28.insuranceservice.exception.ControlNumberNotFoundException;
import com.varunu28.insuranceservice.model.Plan;
import com.varunu28.insuranceservice.model.VerificationRequest;
import com.varunu28.insuranceservice.model.VerificationResponse;
import com.varunu28.insuranceservice.service.VerificationService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecordVerificationController.class)
@Import(RecordVerificationControllerTest.MockConfig.class)
class RecordVerificationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private VerificationService verificationService;

    private VerificationRequest validRequest;
    private VerificationResponse validResponse;

    @BeforeEach
    void setUp() {
        reset(verificationService);

        validRequest = new VerificationRequest("12345", "POLICY123", LocalDate.of(2025, 2, 1));
        validResponse = new VerificationResponse(
            "SUCCESS",
            List.of(new Plan("Primary Care", 80.0, 20.0)), null);
    }

    @Test
    void verifyRecords_success() throws Exception {
        when(verificationService.verify(any(), any(), any())).thenReturn(validResponse);
        mockMvc.perform(post("/api/v1/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.eligiblePlans").isArray())
            .andExpect(jsonPath("$.failureReason").doesNotExist());
    }

    @Test
    void verifyRecords_controlNumberNotFound() throws Exception {
        when(verificationService.verify(any(), any(), any()))
            .thenThrow(new ControlNumberNotFoundException("Control number not found"));

        mockMvc.perform(post("/api/v1/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value("FAILURE"))
            .andExpect(jsonPath("$.failureReason").value("Control number not found"));
    }

    @Test
    void verifyRecords_badRequest() throws Exception {
        when(verificationService.verify(any(), any(), any()))
            .thenThrow(new BadVerificationRequest("Invalid request"));

        mockMvc.perform(post("/api/v1/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value("FAILURE"))
            .andExpect(jsonPath("$.failureReason").value("Invalid request"));
    }

    @Test
    void verifyRecords_validationError_missingFields() throws Exception {
        VerificationRequest invalidRequest = new VerificationRequest(null, null, null);
        mockMvc.perform(post("/api/v1/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void verifyRecords_invalidDateFormat() throws Exception {
        String invalidJson = "{" +
            "\"controlNumber\":\"12345\"," +
            "\"policyNumber\":\"POLICY123\"," +
            "\"requestDate\":\"2025-02-30\"}"; // Invalid date
        mockMvc.perform(post("/api/v1/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void verifyRecords_emptyBody() throws Exception {
        mockMvc.perform(post("/api/v1/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        public VerificationService verificationService() {
            return mock(VerificationService.class);
        }
    }
}

