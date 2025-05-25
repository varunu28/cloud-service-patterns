package com.varunu28.orderpickupservice.config;

import com.varunu28.orderpickupservice.filter.HeaderValidationFilter;
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
        registrationBean.addUrlPatterns("/api/v1/pickups/*");
        return registrationBean;
    }
}
