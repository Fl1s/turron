package org.turron.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.service.entity.SourceEntity;
import org.turron.service.entity.VideoEntity;
import org.turron.service.event.SourceFrameExtractedEvent;
import org.turron.service.event.VideoFrameExtractedEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractionProducer {
    private final KafkaTemplate<String, VideoFrameExtractedEvent> videoFrameExtractedEventTemplate;
    private final KafkaTemplate<String, SourceFrameExtractedEvent> sourceFrameExtractedEventTemplate;

    public void sendVideoFrameExtractedEvent(String correlationId, VideoEntity extractedFrame) {
        VideoFrameExtractedEvent event = new VideoFrameExtractedEvent(
                correlationId,
                extractedFrame.getFrameId(),
                extractedFrame.getVideoId(),
                extractedFrame.getFrameUrl()
        );

        log.info("Sending VideoFrameExtractedEvent [frameId={}, videoId={}, imageUrl={}, correlationId={}] to topic 'video.frames.extracted'",
                event.getFrameId(),
                event.getVideoId(),
                event.getFrameUrl(),
                event.getCorrelationId()
        );
        log.debug("Published VideoFrameExtractedEvent: {}", event);

        videoFrameExtractedEventTemplate
                .send("video.frames.extracted", event)
                .thenAccept(sendResult -> log.info("VideoFrameExtractedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send VideoFrameExtractedEvent [frameId={}, correlationId={}]: {}",
                            event.getFrameId(), event.getCorrelationId(), ex.getMessage(), ex);
                    return null;
                });
    }
    public void sendSourceFrameExtractedEvent(String correlationId, SourceEntity extractedFrame) {
        SourceFrameExtractedEvent event = new SourceFrameExtractedEvent(
                correlationId,
                extractedFrame.getFrameId(),
                extractedFrame.getSourceId(),
                extractedFrame.getFrameUrl()
        );

        log.info("Sending SourceFrameExtractedEvent [frameId={}, sourceId={}, imageUrl={}, correlationId={}] to topic 'source.frames.extracted'",
                event.getFrameId(),
                event.getSourceId(),
                event.getFrameUrl(),
                event.getCorrelationId()
        );
        log.debug("Published SourceFrameExtractedEvent: {}", event);

        sourceFrameExtractedEventTemplate
                .send("source.frames.extracted", event)
                .thenAccept(sendResult -> log.info("SourceFrameExtractedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send SourceFrameExtractedEvent [frameId={}, correlationId={}]: {}",
                            event.getFrameId(), event.getCorrelationId(), ex.getMessage(), ex);
                    return null;
                });
    }
}
