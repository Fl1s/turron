package org.turron.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.service.event.FrameExtractedEvent;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractionProducer {
    private final KafkaTemplate<String, FrameExtractedEvent> extractedEventTemplate;

    public void sendFramesExtractedEvent(FrameExtractedEvent event) {

        log.info("Sending VideoUploadedEvent [frameId={}, videoId={}, imageUrl={}, correlationId={}] to topic 'video.frames.extracted'",
                event.getFrameId(),
                event.getVideoId(),
                event.getFrameUrl(),
                event.getCorrelationId()
        );

        extractedEventTemplate
                .send("video.frames.extracted", event)
                .thenAccept(sendResult -> log.info("FrameExtractedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send FrameExtractedEvent [frameId={}, correlationId={}]: {}",
                            event.getFrameId(), event.getCorrelationId(), ex.getMessage(), ex);
                    return null;
                });
    }
}
