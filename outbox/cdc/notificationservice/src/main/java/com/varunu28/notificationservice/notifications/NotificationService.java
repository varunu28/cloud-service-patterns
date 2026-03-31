package com.varunu28.notificationservice.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void processNotification(SendNotificationDto request) {
        logger.info(
            """
                Sending notification with idempotency key: {}
                for event type: {}
                for customer: {}
                for order: {}
                with amount: {}
                """,
            request.idempotencyKey(),
            request.eventType(),
            request.customerName(),
            request.orderId(),
            request.amount());
    }
}
