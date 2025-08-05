package com.varunu28.healthrecordservice.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.varunu28.healthrecordservice.exception.InsuranceVerificationException;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HealthRecordControllerAdvice {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @ExceptionHandler(InsuranceVerificationException.class)
    public ResponseEntity<ErrorResponseBody> handleInsuranceVerificationException(InsuranceVerificationException ex)
        throws IOException {
        ErrorResponseBody errorResponseBody =
            OBJECT_MAPPER.readValue(ex.getResponseBodyAsByteArray(), ErrorResponseBody.class);
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseBody);
        } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return ResponseEntity.badRequest().body(errorResponseBody);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseBody);
        }
    }

    private record ErrorResponseBody(String status, String failureReason) {}
}
