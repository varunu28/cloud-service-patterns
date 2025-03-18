package com.varunu28.greetingservice.controller;

import com.varunu28.greetingservice.properties.GreetingProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/greetings")
public class GreetingController {

    private final GreetingProperties greetingProperties;

    public GreetingController(GreetingProperties greetingProperties) {
        this.greetingProperties = greetingProperties;
    }

    @GetMapping("/{locale}")
    public String getGreetingByLocale(@PathVariable String locale) {
        return greetingProperties.getGreetings()
            .getOrDefault(locale, greetingProperties.getGreeting());
    }

    @GetMapping
    public String getGreeting() {
        return greetingProperties.getGreeting();
    }
}
