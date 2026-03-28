package com.varunu28.orderservice.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.varunu28.orderservice.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RelayWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelayWorker.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final int SCHEDULE_TIME_PERIOD = 5000; // 5 seconds

    private final OutboxRepository outboxRepository;

    public RelayWorker(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(fixedRate = SCHEDULE_TIME_PERIOD)
    public void processOutbox() {
        outboxRepository.findAllPending()
            .forEach(outbox -> {
                try {
                    LOGGER.info(
                        "Processing outbox with id: {} Payload: {}",
                        outbox.getId(),
                        MAPPER.readValue(outbox.getPayload(), PayloadData.class));
                } catch (JsonProcessingException e) {
                    LOGGER.info("Error processing outbox with id: {}", e.getMessage());
                }
            });
    }
}
