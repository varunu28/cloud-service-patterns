package com.varunu28.webapp.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "config")
public class ServiceProperties {

    private String nameServiceUrl;
    private String greetingServiceUrl;

    public String getNameServiceUrl() {
        return nameServiceUrl;
    }

    public void setNameServiceUrl(String nameServiceUrl) {
        this.nameServiceUrl = nameServiceUrl;
    }

    public String getGreetingServiceUrl() {
        return greetingServiceUrl;
    }

    public void setGreetingServiceUrl(String greetingServiceUrl) {
        this.greetingServiceUrl = greetingServiceUrl;
    }
}
