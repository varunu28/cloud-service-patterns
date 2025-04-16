package com.varunu28.webapp.service;

import com.varunu28.webapp.properties.ServiceProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebService {

    private final ServiceProperties serviceProperties;
    private final RestTemplate restTemplate;

    public WebService(ServiceProperties serviceProperties, RestTemplate restTemplate) {
        this.serviceProperties = serviceProperties;
        this.restTemplate = restTemplate;
    }

    public String buildGreeting() {
        String name = restTemplate.getForObject(serviceProperties.getNameServiceUrl(), String.class);
        String greeting = restTemplate.getForObject(serviceProperties.getGreetingServiceUrl(), String.class);
        return greeting + " " + name;
    }

    public String buildGreetingForLocale(String locale) {
        String name = restTemplate.getForObject(serviceProperties.getNameServiceUrl(), String.class);
        String greeting = restTemplate.getForObject(serviceProperties.getGreetingServiceUrl() + "/" + locale, String.class);
        return greeting + " " + name;
    }
}
