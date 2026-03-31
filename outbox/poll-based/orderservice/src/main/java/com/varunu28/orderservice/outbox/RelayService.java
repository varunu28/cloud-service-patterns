package com.varunu28.orderservice.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.varunu28.orderservice.dto.SendNotificationDto;
import com.varunu28.orderservice.repository.OutboxProjection;
import com.varunu28.orderservice.repository.OutboxRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RelayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelayService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int MAX_RETRIES = 3;

    private static final String ORDER_CREATED_TOPIC = "order-created";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxRepository outboxRepository;

    public RelayService(KafkaTemplate<String, String> kafkaTemplate, OutboxRepository outboxRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public void queryAndProcessOrderEvents() {
        Map<OutboxProjection, Boolean> processingResult = new HashMap<>();
        for (OutboxProjection outboxProjection : outboxRepository.findAllPending()) {
            try {
                PayloadData payloadData = MAPPER.readValue(outboxProjection.getPayload(), PayloadData.class);
                LOGGER.info(
                    "Processing outbox with id: {} Payload: {}",
                    outboxProjection.getId(), payloadData);
                boolean successful = publishToKafka(outboxProjection, payloadData);
                processingResult.put(outboxProjection, successful);
            } catch (JsonProcessingException e) {
                LOGGER.info("Error processing outbox with id: {}", e.getMessage());
                processingResult.put(outboxProjection, false);
            }
        }
        updateOutboxEntities(processingResult);
    }

    private boolean publishToKafka(OutboxProjection outboxProjection, PayloadData payloadData) {
        String idempotencyKey = String.format("idempotency-key-%s", outboxProjection.getId());
        SendNotificationDto sendNotificationDto = new SendNotificationDto(
            idempotencyKey,
            outboxProjection.getEventType(),
            payloadData.customerName(),
            payloadData.orderId(),
            payloadData.amount()
        );
        try {
            String message = MAPPER.writeValueAsString(sendNotificationDto);
            kafkaTemplate.send(ORDER_CREATED_TOPIC, String.valueOf(outboxProjection.getId()), message).get();
            return true;
        } catch (Exception e) {
            LOGGER.error(
                "Failed to publish to Kafka for outbox id: {}. Error: {}",
                outboxProjection.getId(), e.getMessage());
            return false;
        }
    }

    private void updateOutboxEntities(Map<OutboxProjection, Boolean> processingResult) {
        List<Long> processedIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();
        List<Long> retryIds = new ArrayList<>();

        for (Map.Entry<OutboxProjection, Boolean> entry : processingResult.entrySet()) {
            OutboxProjection projection = entry.getKey();
            if (entry.getValue()) {
                processedIds.add(projection.getId());
            } else if (projection.getRetryCount() >= MAX_RETRIES) {
                failedIds.add(projection.getId());
            } else {
                retryIds.add(projection.getId());
            }
        }
        if (!processedIds.isEmpty()) {
            outboxRepository.markAsProcessed(processedIds);
        }
        if (!failedIds.isEmpty()) {
            outboxRepository.markAsFailed(failedIds);
        }
        if (!retryIds.isEmpty()) {
            outboxRepository.incrementRetry(retryIds);
        }
    }
}
