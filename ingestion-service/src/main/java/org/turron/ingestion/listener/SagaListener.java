package org.turron.ingestion.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.turron.ingestion.config.SagaOrchestrator;
import org.turron.ingestion.event.ThoughtEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaListener {

    private final SagaOrchestrator sagaOrchestrator;

    @KafkaListener(
            topics = "thought-status",
            groupId = "ingestion-service-group",
            containerFactory = "kafkaListenerContainerFactoryThoughtEvent"
    )
    public void handleThoughtStatus(ThoughtEvent result) {
        log.info("[SagaListener] Received thought status update. correlationId={}, status={}",
                result.getCorrelationId(), result.getStatus());
        sagaOrchestrator.processResult(result);
    }
}

