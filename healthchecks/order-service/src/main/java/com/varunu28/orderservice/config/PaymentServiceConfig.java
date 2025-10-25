package com.varunu28.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentServiceConfig {

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    @Bean("paymentServiceRestClient")
    public RestClient paymentServiceRestClient() {
        return RestClient.builder()
            .baseUrl(paymentServiceUrl)
            .build();
    }
}
