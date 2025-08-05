package com.varunu28.healthrecordservice.exception;

import java.nio.charset.Charset;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

public class InsuranceVerificationException extends HttpClientErrorException {
    public InsuranceVerificationException(
        HttpStatusCode statusCode,
        String statusText,
        byte[] body,
        Charset responseCharset) {
        super(statusCode, statusText, body, responseCharset);
    }
}
