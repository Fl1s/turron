package org.turron.memory.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.turron.memory.event.ThoughtEvent;
import org.turron.memory.service.MemoryService;

@Service
@RequiredArgsConstructor
public class MemoryListener {
    private final MemoryService memoryService;
    @KafkaListener(topics = "ingested-thought", groupId = "ingestion-service-group",
            containerFactory = "kafkaListenerContainerFactoryThoughtEvent")
    public void handleThoughtStore(ThoughtEvent thoughtEvent){
        memoryService.storeThought(thoughtEvent);
    }
}
