package com.varunu28.orderpickupservice.controller;

import com.varunu28.orderpickupservice.model.CreatePickupRequest;
import com.varunu28.orderpickupservice.model.Pickup;
import com.varunu28.orderpickupservice.model.UpdatePickupRequest;
import com.varunu28.orderpickupservice.service.OrderPickupService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pickups")
public class OrderPickupController {

    private final OrderPickupService orderPickupService;

    public OrderPickupController(OrderPickupService orderPickupService) {
        this.orderPickupService = orderPickupService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Pickup> getPickup(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(orderPickupService.getPickupById(id));
    }

    @PostMapping
    public ResponseEntity<Pickup> createPickup(@RequestBody CreatePickupRequest createPickupRequest) {
        return ResponseEntity.ok(
            orderPickupService.createPickup(
                createPickupRequest.assigneeId(), createPickupRequest.orderId(), createPickupRequest.description()));
    }

    @PutMapping("{id}")
    public ResponseEntity<Pickup> updatePickup(@PathVariable("id") UUID id, @RequestBody UpdatePickupRequest updatePickupRequest) {
        return ResponseEntity.ok(
            orderPickupService.updatePickupById(id, updatePickupRequest.description(), updatePickupRequest.completed()));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Pickup> cancelPickup(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(orderPickupService.cancelPickup(id));
    }
}

