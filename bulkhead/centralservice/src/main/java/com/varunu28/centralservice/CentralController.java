package com.varunu28.centralservice;

import org.springframework.http.HttpStatus;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/v1")
public class CentralController {

    private final RestClient serviceaRestClient;
    private final RestClient servicebRestClient;

    public CentralController(RestClient serviceaRestClient, RestClient servicebRestClient) {
        this.serviceaRestClient = serviceaRestClient;
        this.servicebRestClient = servicebRestClient;
    }

    @GetMapping("/servicea")
    @ResponseStatus(HttpStatus.OK)
    @ConcurrencyLimit(limit = 5, policy = ConcurrencyLimit.ThrottlePolicy.REJECT)
    public String servicea() {
        return serviceaRestClient.get()
                .retrieve()
                .body(String.class);
    }

    @GetMapping("/serviceb")
    @ResponseStatus(HttpStatus.OK)
    @ConcurrencyLimit(limit = 5, policy = ConcurrencyLimit.ThrottlePolicy.REJECT)
    public String serviceb() {
        return servicebRestClient.get()
                .retrieve()
                .body(String.class);
    }
}
