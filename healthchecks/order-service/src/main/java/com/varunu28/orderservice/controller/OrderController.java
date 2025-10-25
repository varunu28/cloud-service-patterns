package com.varunu28.orderservice.controller;

import com.varunu28.orderservice.dto.CreateOrderRequest;
import com.varunu28.orderservice.service.OrderService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/orders", produces = APPLICATION_JSON_VALUE)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        UUID orderId = orderService.createOrder(
            createOrderRequest.description(),
            createOrderRequest.amount(),
            createOrderRequest.customerId()
        );
        return ResponseEntity.ok(orderId);
    }
}
