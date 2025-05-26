package org.turron.upload.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.upload.event.FrameExtractedEvent;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractionProducer {
    private final KafkaTemplate<String, FrameExtractedEvent> extractedEventTemplate;

    public void sendFramesExtractedEvent(FrameExtractedEvent event) {
        String correlationId = UUID.randomUUID().toString();


        log.info("Sending VideoUploadedEvent [videoId={}, frameId={}, imageUrl={}, correlationId={}] to topic 'frame.extracted'",
                event.getVideoId(),
                event.getFrameId(),
                event.getImageUrl(),
                correlationId
        );

        extractedEventTemplate
                .send("frame.extracted", event)
                .thenAccept(sendResult -> log.info("FrameExtractedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send FrameExtractedEvent [videoId={}, correlationId={}]: {}",
                            event.getVideoId(), correlationId, ex.getMessage(), ex);
                    return null;
                });
    }
}
