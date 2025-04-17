package com.varunu28.webapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WebService {

    private static final String GREETING_SERVICE_ENDPOINT = "/api/v1/greetings";
    private static final String NAME_SERVICE_ENDPOINT = "/api/v1/names";

    private final RestClient restClient;

    public WebService(RestClient restClient) {
        this.restClient = restClient;
    }

    public String buildGreeting() {
        String name = restClient.get()
                .uri("http://name-service" + NAME_SERVICE_ENDPOINT)
                .retrieve()
                .body(String.class);
        String greeting = restClient.get()
                .uri("http://greeting-service" + GREETING_SERVICE_ENDPOINT)
                .retrieve()
                .body(String.class);
        return greeting + " " + name;
    }

    public String buildGreetingForLocale(String locale) {
        String name = restClient.get()
                .uri("http://name-service" + NAME_SERVICE_ENDPOINT)
                .retrieve()
                .body(String.class);
        String greeting = restClient.get()
                .uri("http://greeting-service" + GREETING_SERVICE_ENDPOINT + "/" + locale)
                .retrieve()
                .body(String.class);
        return greeting + " " + name;
    }
}
