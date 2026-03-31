package com.varunu28.notificationservice.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listener to consume messages from `order-created` topic. This listener removes the requirement for sync invocation
 * of notification from order-service
 */
@Component
public class OrderEventListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final NotificationService notificationService;

    public OrderEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "order-created", groupId = "notification-group")
    public void handleOrderCreated(String message) {
        try {
            logger.info("Received order-created event: {}", message);
            SendNotificationDto notification = OBJECT_MAPPER.readValue(message, SendNotificationDto.class);
            notificationService.processNotification(notification);
        } catch (Exception e) {
            logger.error("Failed to process order-created event: {}", message, e);
        }
    }
}
