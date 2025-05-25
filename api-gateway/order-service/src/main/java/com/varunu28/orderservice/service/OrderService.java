package com.varunu28.orderservice.service;

import com.varunu28.orderservice.model.Order;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final Map<UUID, Order> orders;

    public OrderService() {
        orders = new HashMap<>();
    }

    public Order getOrderById(UUID id) {
        if (!orders.containsKey(id)) {
            throw new NoSuchElementException("Order with id " + id + " not found");
        }
        Order order = orders.get(id);
        if (order.isDeleted()) {
            throw new NoSuchElementException("Order with id " + id + " is deleted");
        }
        return order;
    }

    public Order createOrder(String description) {
        ZonedDateTime now = ZonedDateTime.now();
        Order order = new Order(UUID.randomUUID(), description, now, now);
        orders.put(order.getId(), order);
        return order;
    }

    public Order updateOrder(UUID id, String previousDescription, String updatedDescription) {
        if (!orders.containsKey(id)) {
            throw new NoSuchElementException("Order with id " + id + " not found");
        }
        Order order = orders.get(id);
        order.setDescription(updatedDescription, previousDescription);
        return order;
    }

    public Order deleteOrder(UUID id) {
        if (!orders.containsKey(id)) {
            throw new NoSuchElementException("Order with id " + id + " not found");
        }
        Order order = orders.get(id);
        order.markAsDeleted();
        return order;
    }
}
