package com.varunu28.webapp.controller;

import com.varunu28.webapp.service.WebService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/web")
public class WebController {

    private final WebService service;

    public WebController(WebService service) {
        this.service = service;
    }

    @GetMapping
    public String greet() {
        return service.buildGreeting();
    }

    @GetMapping("/{locale}")
    public String greetByLocale(@PathVariable String locale) {
        return service.buildGreetingForLocale(locale);
    }
}
