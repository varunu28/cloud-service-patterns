package com.varunu28.notificationservice.notifications;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    @Value("${failure.amount.threshold}")
    private double failureAmountThreshold;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Void> sendNotification(@Valid @RequestBody SendNotificationDto request) {
        // Simulate a failure
        if (request.amount() > failureAmountThreshold) {
            return ResponseEntity.internalServerError().build();
        }
        notificationService.processNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
