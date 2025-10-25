package com.varunu28.orderservice.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varunu28.orderservice.model.Order;
import com.varunu28.orderservice.repository.OrderRepository;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestClient paymentServiceRestClient;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(
        OrderRepository orderRepository,
        @Qualifier("paymentServiceRestClient") RestClient paymentServiceRestClient,
        RabbitTemplate rabbitTemplate) {
        this.paymentServiceRestClient = paymentServiceRestClient;
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public UUID createOrder(String description, double amount, UUID customerId) {
        // Generate an idempotency key for the order
        var idempotencyKey = String.format("%s-%f-%s", customerId, amount, description);
        // Call payment service to process payment
        UUID paymentId = registerPayment(amount, customerId, idempotencyKey);
        // Publish a payment created event
        rabbitTemplate.convertAndSend("payment.events", "payment.created", paymentId);
        // Save order to the database
        Order order = new Order(description, amount, customerId, paymentId, idempotencyKey);
        Order savedOrder = orderRepository.save(order);
        // Publish an order created event
        rabbitTemplate.convertAndSend("order.events", "order.created", savedOrder.getId());
        return savedOrder.getId();
    }

    private UUID registerPayment(double amount, UUID customerId, String idempotencyKey) {
        CreatePaymentRequest paymentRequest = new CreatePaymentRequest(customerId, amount, idempotencyKey);
        return paymentServiceRestClient.post()
            .body(paymentRequest)
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(UUID.class);
    }

    record CreatePaymentRequest(@JsonProperty("customer_id") UUID customerId,
                                @JsonProperty("amount") double amount,
                                @JsonProperty("idempotency_key") String idempotencyKey) {}
}
