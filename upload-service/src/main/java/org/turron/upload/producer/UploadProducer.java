package org.turron.upload.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.upload.event.VideoUploadedEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadProducer {

    private final KafkaTemplate<String, VideoUploadedEvent> uploadedEventTemplate;

    public void sendUploadedEvent(VideoUploadedEvent videoUploadedEvent) {
        String correlationId = UUID.randomUUID().toString();

        VideoUploadedEvent event = new VideoUploadedEvent(
                correlationId,
                videoUploadedEvent.getVideoId(),
                videoUploadedEvent.getSourceUrl()
        );

        log.info("Sending VideoUploadedEvent [videoId={}, sourceUrl={}, correlationId={}] to topic 'video.uploaded'",
                event.getVideoId(),
                event.getSourceUrl(),
                correlationId
        );

        uploadedEventTemplate
                .send("video.uploaded", event)
                .thenAccept(sendResult -> log.info("VideoUploadedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send VideoUploadedEvent [videoId={}, correlationId={}]: {}",
                            event.getVideoId(), correlationId, ex.getMessage(), ex);
                    return null;
                });
    }
}
