package com.varunu28.centralservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient serviceaRestClient(@Value("${servicea.url}") String serviceaUrl) {
        return RestClient.builder()
                .requestFactory(new SimpleClientHttpRequestFactory())
                .baseUrl(serviceaUrl)
                .build();
    }

    @Bean
    public RestClient servicebRestClient(@Value("${serviceb.url}") String servicebUrl) {
        return RestClient.builder()
                .requestFactory(new SimpleClientHttpRequestFactory())
                .baseUrl(servicebUrl)
                .build();
    }
}
