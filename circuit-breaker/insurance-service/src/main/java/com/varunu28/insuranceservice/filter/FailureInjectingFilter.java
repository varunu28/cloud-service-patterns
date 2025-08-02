package com.varunu28.insuranceservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class FailureInjectingFilter extends OncePerRequestFilter {

    @Value("file:insurance-service/src/main/resources/config.txt")
    private Resource configFile;

    private static final String FAIL_VALUE = "FAIL";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String configValue = readConfig();
        if (configValue != null && configValue.equals(FAIL_VALUE)) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String readConfig() {
        try (var reader = new BufferedReader(new InputStreamReader(configFile.getInputStream()))) {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
