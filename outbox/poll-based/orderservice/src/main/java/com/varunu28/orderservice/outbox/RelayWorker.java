package com.varunu28.orderservice.outbox;

import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RelayWorker {

    private static final int SCHEDULE_TIME_PERIOD = 5;

    private final RelayService relayService;

    public RelayWorker(RelayService relayService) {
        this.relayService = relayService;
    }

    @Scheduled(fixedRate = SCHEDULE_TIME_PERIOD, timeUnit = TimeUnit.SECONDS)
    public void processOutbox() {
        relayService.queryAndProcessOrderEvents();
    }
}
