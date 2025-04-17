package org.turron.ingestion.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.ingestion.event.ThoughtEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThoughtEventProducer {
    private final KafkaTemplate<String, ThoughtEvent> thoughtEventTemplate;

    public void sendThoughtEvent(ThoughtEvent thought) {
        log.info("[Producer] Sending thought event to topic='ingested-thought'. correlationId={}", thought.getCorrelationId());
        thoughtEventTemplate.send("ingested-thought", thought);
    }
}
