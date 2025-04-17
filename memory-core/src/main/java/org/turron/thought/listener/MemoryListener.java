package org.turron.thought.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.turron.thought.event.ThoughtEvent;
import org.turron.thought.service.MemoryService;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemoryListener {
    private final MemoryService memoryService;
    @KafkaListener(topics = "ingested-thought", groupId = "ingestion-service-group",
            containerFactory = "kafkaListenerContainerFactoryThoughtEvent")
    public void handleThoughtStore(ThoughtEvent thoughtEvent){
        log.info("[MemoryListener] Received ingested thought. correlationId={}", thoughtEvent.getCorrelationId());
        memoryService.storeThought(thoughtEvent);
    }
}
