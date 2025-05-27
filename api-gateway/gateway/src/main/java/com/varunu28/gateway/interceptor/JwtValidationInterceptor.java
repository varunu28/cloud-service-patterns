package com.varunu28.gateway.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtValidationInterceptor implements HandlerInterceptor {

    private static final String AUTH_SERVICE_URL = "http://localhost:8003/auth/validate";

    private final RestTemplate restTemplate;

    public JwtValidationInterceptor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        String jwtToken = request.getHeader("Authorization");
        if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
            return false;
        }
        String token = jwtToken.substring(7);
        Boolean isValid;
        try {
            isValid = restTemplate.postForObject(AUTH_SERVICE_URL, token, Boolean.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid JWT token");
            } else {
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error validating JWT token");
            }
            return false;
        }
        if (isValid == null || !isValid) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid JWT token");
            return false;
        }
        return true;
    }
}
