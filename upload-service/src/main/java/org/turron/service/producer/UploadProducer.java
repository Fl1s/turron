package org.turron.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.service.entity.SourceEntity;
import org.turron.service.entity.VideoEntity;
import org.turron.service.event.SourceUploadedEvent;
import org.turron.service.event.VideoUploadedEvent;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadProducer {

    private final KafkaTemplate<String, VideoUploadedEvent> videoUploadedEventTemplate;
    private final KafkaTemplate<String, SourceUploadedEvent> sourceUploadedEventTemplate;

    public void sendVideoUploadedEvent(VideoEntity videoEntity) {
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

        videoUploadedEventTemplate
                .send("video.uploaded", event)
                .thenAccept(sendResult -> log.info("VideoUploadedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send VideoUploadedEvent [videoId={}, correlationId={}]: {}",
                            event.getVideoId(), correlationId, ex.getMessage(), ex);
                    return null;
                });
    }
    public void sendSourceUploadedEvent(SourceEntity videoEntity) {
        String correlationId = UUID.randomUUID().toString();

        SourceUploadedEvent event = new SourceUploadedEvent(
                correlationId,
                videoEntity.getSourceId(),
                videoEntity.getSourceUrl()
        );

        log.info("Sending SourceUploadedEvent [videoId={}, sourceUrl={}, correlationId={}] to topic 'source.uploaded'",
                event.getSourceId(),
                event.getSourceUrl(),
                correlationId
        );

        sourceUploadedEventTemplate
                .send("source.uploaded", event)
                .thenAccept(sendResult -> log.info("SourceUploadedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send SourceUploadedEvent [sourceId={}, correlationId={}]: {}",
                            event.getSourceId(), correlationId, ex.getMessage(), ex);
                    return null;
                });
    }
}
