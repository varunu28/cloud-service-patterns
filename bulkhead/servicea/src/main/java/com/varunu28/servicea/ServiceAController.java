package com.varunu28.servicea;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ServiceAController {

    private static final long SLEEP_TIME = 1000_0; // 10 seconds

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String servicebGet() throws InterruptedException {
        Thread.sleep(SLEEP_TIME);
        return "Service A";
    }
}
