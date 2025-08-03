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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
        if (currentConfigValue.equals(FAIL_VALUE)) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Scheduled(fixedDelay = 100)
    public void updateConfig() {
        LOGGER.debug("Refreshing config");
        String configValue = readConfig();
        if (configValue != null) {
            if (!configValue.trim().equals(currentConfigValue)) {
                LOGGER.debug("Config value changed from {} to {}", currentConfigValue, configValue.trim());
            }
            currentConfigValue = configValue.trim();
            LOGGER.debug("Config updated to: {}", currentConfigValue);
        }
    }

    private String readConfig() {
        try (var reader = new BufferedReader(new InputStreamReader(configFile.getInputStream()))) {
            return reader.readLine();
        } catch (IOException e) {
            LOGGER.error("Error reading config file: {}", e.getMessage());
            return null;
        }
    }
}
