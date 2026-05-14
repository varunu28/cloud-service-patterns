package com.saga.orderservice

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigInteger

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(private val service: OrderService) {

    @PostMapping
    fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<BigInteger> {
        val orderId = service.createOrder(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId)
    }

    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: BigInteger): ResponseEntity<Order> {
        return ResponseEntity.status(HttpStatus.OK).body(service.getOrderById(orderId))
    }
}