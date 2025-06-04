package org.turron.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.service.entity.SourceEntity;
import org.turron.service.entity.VideoEntity;
import org.turron.service.event.SourceFrameHashedEvent;
import org.turron.service.event.VideoFrameHashedEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashingProducer {
    private final KafkaTemplate<String, VideoFrameHashedEvent> videoHashedEventTemplate;
    private final KafkaTemplate<String, SourceFrameHashedEvent> sourceHashedEventTemplate;

    public void sendVideoFramesHashedEvent(String correlationId, VideoEntity entity) {
        VideoFrameHashedEvent event = new VideoFrameHashedEvent(
                entity.getHashId(),
                entity.getVideoId(),
                entity.getFrameId(),
                entity.getFrameHash(),
                correlationId
        );

        log.info("Sending VideoFrameHashedEvent [hashId={}, videoId={}, frameId={}, frameHash={}, correlationId={}] to topic 'video.frames.hashed'",
                event.getHashId(),
                event.getVideoId(),
                event.getFrameId(),
                event.getFrameHash(),
                event.getCorrelationId()
        );

        videoHashedEventTemplate
                .send("video.frames.hashed", event)
                .thenAccept(sendResult -> log.info("VideoFrameHashedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send VideoFrameHashedEvent [videoId={}, correlationId={}]: {}",
                            event.getVideoId(), event.getCorrelationId(), ex.getMessage(), ex);
                    return null;
                });
    }
    public void sendSourceFramesHashedEvent(String correlationId, SourceEntity entity) {
        SourceFrameHashedEvent event = new SourceFrameHashedEvent(
                entity.getHashId(),
                entity.getSourceId(),
                entity.getFrameId(),
                entity.getFrameHash(),
                correlationId
        );

        log.info("Sending SourceFrameHashedEvent [hashId={}, sourceId={}, frameId={}, frameHash={}, correlationId={}] to topic 'source.frames.hashed'",
                event.getHashId(),
                event.getSourceId(),
                event.getFrameId(),
                event.getFrameHash(),
                event.getCorrelationId()
        );

        sourceHashedEventTemplate
                .send("source.frames.hashed", event)
                .thenAccept(sendResult -> log.info("SourceFrameHashedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send SourceFrameHashedEvent [sourceId={}, correlationId={}]: {}",
                            event.getSourceId(), event.getCorrelationId(), ex.getMessage(), ex);
                    return null;
                });
    }
}
