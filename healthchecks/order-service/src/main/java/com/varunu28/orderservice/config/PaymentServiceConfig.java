package com.varunu28.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentServiceConfig {

    @Bean("paymentServiceRestClient")
    public RestClient paymentServiceRestClient() {
        return RestClient.builder()
            .build();
    }
}
