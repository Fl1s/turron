package org.turron.ingestion.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.turron.ingestion.dto.ThoughtDto;
import org.turron.ingestion.event.ThoughtEvent;
import org.turron.ingestion.producer.ThoughtEventProducer;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestrator {
    private final ThoughtEventProducer thoughtEventProducer;

    public void startSaga(ThoughtDto thoughtDto) {
        String correlationId = UUID.randomUUID().toString();
        log.info("[Saga] Starting saga for correlationId={}", correlationId);
        thoughtEventProducer.sendThoughtEvent(new ThoughtEvent(
                correlationId,
                thoughtDto.getSource(),
                thoughtDto.getType(),
                thoughtDto.getContent(),
                thoughtDto.getTags(),
                thoughtDto.getCreatedAt(),
                thoughtDto.getExpiresAt(),
                thoughtDto.getImportance(),
                "PENDING",
                "INGESTION"
        ));
    }

    public void processResult(ThoughtEvent result) {
        switch (result.getStatus()) {
            case "SUCCESS" -> log.info("[Saga] Successfully completed saga. correlationId={}", result.getCorrelationId());
            case "FAILURE" -> {
                log.warn("[Saga] Saga failed. Starting compensation. correlationId={}", result.getCorrelationId());
                sendCompensation(result);
            }
            default -> log.error("[Saga] Unknown saga status received: {} for correlationId={}", result.getStatus(), result.getCorrelationId());
        }
    }


    private void sendCompensation(ThoughtEvent failedEvent) {
        log.info("[Saga] Sending compensation event. correlationId={}", failedEvent.getCorrelationId());
        ThoughtEvent compensationEvent = new ThoughtEvent(
                failedEvent.getCorrelationId(),
                failedEvent.getSource(),
                failedEvent.getType(),
                failedEvent.getContent(),
                failedEvent.getTags(),
                failedEvent.getCreatedAt(),
                failedEvent.getExpiresAt(),
                failedEvent.getImportance(),
                "COMPENSATION",
                "INGESTION"
        );

        thoughtEventProducer.sendThoughtEvent(compensationEvent);
    }

}
