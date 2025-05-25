package com.varunu28.orderservice.controller;

import com.varunu28.orderservice.model.CreateOrderRequest;
import com.varunu28.orderservice.model.Order;
import com.varunu28.orderservice.model.UpdateOrderRequest;
import com.varunu28.orderservice.service.OrderService;
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
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Order> getOrder(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<Order> getOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        return ResponseEntity.ok(orderService.createOrder(createOrderRequest.description()));
    }

    @PutMapping("{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable("id") UUID id, @RequestBody UpdateOrderRequest updateOrderRequest) {
        return ResponseEntity.ok(
            orderService.updateOrder(id, updateOrderRequest.previousDescription(), updateOrderRequest.updatedDescription()));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(orderService.deleteOrder(id));
    }
}
