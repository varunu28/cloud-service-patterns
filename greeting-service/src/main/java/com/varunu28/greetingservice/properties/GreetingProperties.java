package com.varunu28.greetingservice.properties;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@ConfigurationProperties(prefix = "config")
@RefreshScope
public class GreetingProperties {

    private String greeting;
    private Map<String, String> greetings;

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public Map<String, String> getGreetings() {
        return greetings;
    }

    public void setGreetings(Map<String, String> greetings) {
        this.greetings = greetings;
    }
}
