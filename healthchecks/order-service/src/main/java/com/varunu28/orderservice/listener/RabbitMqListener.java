package com.varunu28.orderservice.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqListener.class);

    @RabbitListener(queues = "order-created-queue")
    public void handleOrderEvents(String message) {
        LOGGER.info("Received message for order: {}", message);
    }

    @RabbitListener(queues = "payment-created-queue")
    public void handlePaymentEvents(String message) {
        LOGGER.info("Received message for payment: {}", message);
    }
}
