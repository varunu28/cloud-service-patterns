package com.varunu28.gateway.config;

import com.varunu28.gateway.interceptor.JwtValidationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtValidationInterceptor jwtValidationInterceptor;

    public WebMvcConfig(JwtValidationInterceptor jwtValidationInterceptor) {
        this.jwtValidationInterceptor = jwtValidationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtValidationInterceptor)
            .addPathPatterns("/api/v1/**")
            .excludePathPatterns("/auth/login", "/auth/register");
    }
}
