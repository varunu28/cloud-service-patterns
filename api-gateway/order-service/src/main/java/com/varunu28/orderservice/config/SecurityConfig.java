package com.varunu28.orderservice.config;

import com.varunu28.orderservice.filter.HeaderValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<HeaderValidationFilter> headerValidationFilterFilterRegistration(
        HeaderValidationFilter filter) {
        FilterRegistrationBean<HeaderValidationFilter> registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setOrder(1);
        registrationBean.addUrlPatterns("/api/v1/orders/*");
        return registrationBean;
    }
}
