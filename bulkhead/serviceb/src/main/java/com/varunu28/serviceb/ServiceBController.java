package com.varunu28.serviceb;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ServiceBController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String getServiceB() {
        return "Service B";
    }
}
