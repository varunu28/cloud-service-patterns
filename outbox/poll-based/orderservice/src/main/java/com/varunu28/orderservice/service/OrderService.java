package com.varunu28.orderservice.service;

import com.varunu28.orderservice.dto.CreateOrderDto;
import com.varunu28.orderservice.dto.OrderDto;
import com.varunu28.orderservice.model.Order;
import com.varunu28.orderservice.model.Outbox;
import com.varunu28.orderservice.model.OutboxEventType;
import com.varunu28.orderservice.repository.OrderRepository;
import com.varunu28.orderservice.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;

    public OrderService(OrderRepository orderRepository, OutboxRepository outboxRepository) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public OrderDto createOrder(CreateOrderDto request) {
        Order order = new Order(request.customerName(), request.amount());
        Order savedOrder = orderRepository.save(order);
        var payload = """
            {
                "order_id": %d,
                "customer_name": "%s",
                "amount": %s
            }
            """.formatted(savedOrder.getId(), savedOrder.getCustomerName(), savedOrder.getAmount());
        Outbox outbox = new Outbox(OutboxEventType.ORDER_CREATED.getValue(), payload);
        outboxRepository.save(outbox);
        return OrderDto.from(savedOrder);
    }
}
