package com.varunu28.insuranceservice.config;

import com.varunu28.insuranceservice.filter.FailureInjectingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FailureConfig {

    @Bean
    public FilterRegistrationBean<FailureInjectingFilter> failureInjectingFilterFilterRegistration(
        FailureInjectingFilter filter) {
        FilterRegistrationBean<FailureInjectingFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(1);
        registration.addUrlPatterns("/api/v1/verify");
        return registration;
    }
}
