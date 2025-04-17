package org.turron.memory.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.memory.event.ThoughtEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThoughtResultProducer {
    private final KafkaTemplate<String, ThoughtEvent> kafkaTemplate;
    public void sendResult(ThoughtEvent event, boolean success) {
        log.info("[Producer] Sending thought result. correlationId={}, status={}", event.getCorrelationId(), event.getStatus());
        event.setStatus(success ? "SUCCESS" : "FAILURE");
        event.setStep("MEMORY_CORE");

        kafkaTemplate.send("thought-status", event);
    }
}

