package com.varunu28.nameservice.controller;

import com.varunu28.nameservice.properties.NameProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/names")
public class NameController {

    private final NameProperties nameProperties;

    public NameController(NameProperties nameProperties) {
        this.nameProperties = nameProperties;
    }

    @GetMapping
    public String getName() {
        return nameProperties.getName();
    }
}
