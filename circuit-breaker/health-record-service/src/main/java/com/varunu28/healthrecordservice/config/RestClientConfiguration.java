package com.varunu28.healthrecordservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Value("${insurance.service.url}")
    private String insuranceServiceUrl;

    @Bean("insuranceServiceRestClient")
    public RestClient restClient() {
        return RestClient.builder()
            .baseUrl(insuranceServiceUrl)
            .build();
    }
}
