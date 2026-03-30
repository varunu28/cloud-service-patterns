package com.varunu28.orderservice.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.varunu28.orderservice.dto.SendNotificationDto;
import com.varunu28.orderservice.repository.OutboxProjection;
import com.varunu28.orderservice.repository.OutboxRepository;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RelayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelayService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final RestClient notificationServiceRestClient;
    private final OutboxRepository outboxRepository;

    public RelayService(RestClient notificationServiceRestClient, OutboxRepository outboxRepository) {
        this.notificationServiceRestClient = notificationServiceRestClient;
        this.outboxRepository = outboxRepository;
    }

    public void queryAndProcessOrderEvents() {
        Map<OutboxProjection, Boolean> processingResult = new HashMap<>();
        for (OutboxProjection outboxProjection : outboxRepository.findAllPending()) {
            try {
                PayloadData payloadData = MAPPER.readValue(outboxProjection.getPayload(), PayloadData.class);
                LOGGER.info(
                    "Processing outbox with id: {} Payload: {}",
                    outboxProjection.getId(), payloadData);
                boolean successful = invokeNotificationRestEndpoint(outboxProjection, payloadData);
                processingResult.put(outboxProjection, successful);
            } catch (JsonProcessingException e) {
                LOGGER.info("Error processing outbox with id: {}", e.getMessage());
                processingResult.put(outboxProjection, false);
            }
        }
        updateOutboxEntities(processingResult);
    }

    private boolean invokeNotificationRestEndpoint(OutboxProjection outboxProjection, PayloadData payloadData) {
        String idempotencyKey = String.format("idempotency-key-%s", outboxProjection.getId());
        SendNotificationDto sendNotificationDto = new SendNotificationDto(
            idempotencyKey,
            outboxProjection.getEventType(),
            payloadData.customerName(),
            payloadData.orderId(),
            payloadData.amount()
        );
        ResponseEntity<Void> response = notificationServiceRestClient.post()
            .body(sendNotificationDto)
            .retrieve()
            .toEntity(Void.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    private void updateOutboxEntities(Map<OutboxProjection, Boolean> processingResult) {
        // TODO: Add implementation for updating status of outbox or resetting the next_retry_at
    }
}
