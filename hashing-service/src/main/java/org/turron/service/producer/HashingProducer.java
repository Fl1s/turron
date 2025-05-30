package org.turron.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.service.entity.HashEntity;
import org.turron.service.event.FrameHashedEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashingProducer {
    private final KafkaTemplate<String, FrameHashedEvent> extractedEventTemplate;

    public void sendFramesHashedEvent(String correlationId, HashEntity entity) {
        FrameHashedEvent event = new FrameHashedEvent(
                correlationId,
                entity.getHashId(),
                entity.getVideoId(),
                entity.getFrameId(),
                entity.getFrameHash()
        );

        log.info("Sending VideoUploadedEvent [hashId={}, videoId={}, frameId={}, frameHash={}, correlationId={}] to topic 'video.frames.hashed'",
                event.getHashId(),
                event.getVideoId(),
                event.getFrameId(),
                event.getFrameHash(),
                event.getCorrelationId()
        );

        extractedEventTemplate
                .send("video.frames.hashed", event)
                .thenAccept(sendResult -> log.info("FrameHashedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send FrameHashedEvent [videoId={}, correlationId={}]: {}",
                            event.getVideoId(), event.getCorrelationId(), ex.getMessage(), ex);
                    return null;
                });
    }
}
