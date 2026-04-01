package com.varunu28.orderservice.repository;

import java.time.LocalDateTime;

public interface OutboxProjection {

    Long getId();

    String getEventType();

    String getPayload();

    int getRetryCount();

    LocalDateTime getNextRetryAt();
}
