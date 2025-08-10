package com.varunu28.insuranceservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class FailureInjectingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailureInjectingFilter.class);

    @Value("file:/app/config/config.txt")
    private Resource configFile;

    private static final String FAIL_VALUE = "FAIL";
    private volatile String currentConfigValue = "PASS";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        LOGGER.debug("Injecting failure");
        if (currentConfigValue.equals(FAIL_VALUE)) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"status\":\"FAILURE\",\"failureReason\":\"Injected failure\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Scheduled(fixedDelay = 100)
    public void updateConfig() {
        String configValue = readConfig();
        if (configValue != null) {
            currentConfigValue = configValue.trim();
        }
    }

    private String readConfig() {
        try (var reader = new BufferedReader(new InputStreamReader(configFile.getInputStream()))) {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
