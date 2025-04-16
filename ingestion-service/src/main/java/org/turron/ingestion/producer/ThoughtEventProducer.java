package org.turron.ingestion.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.ingestion.event.ThoughtEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThoughtEventProducer {
    private final KafkaTemplate<String, ThoughtEvent> thoughtEventTemplate;

    public void sendThoughtEvent(ThoughtEvent thought) {
        thoughtEventTemplate.send(
                "ingested-thought",
                new ThoughtEvent(
                        UUID.randomUUID().toString(),
                        thought.getSource(),
                        thought.getType(),
                        thought.getContent(),
                        thought.getTags(),
                        thought.getCreatedAt(),
                        thought.getExpiresAt(),
                        thought.getImportance()
                )
        );
    }
}
