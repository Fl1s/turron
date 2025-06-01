package org.turron.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.service.entity.VideoEntity;
import org.turron.service.event.VideoUploadedEvent;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadProducer {

    private final KafkaTemplate<String, VideoUploadedEvent> uploadedEventTemplate;

    public void sendUploadedEvent(VideoEntity videoEntity) {
        String correlationId = UUID.randomUUID().toString();

        VideoUploadedEvent event = new VideoUploadedEvent(
                correlationId,
                videoEntity.getVideoId(),
                videoEntity.getSourceUrl()
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
