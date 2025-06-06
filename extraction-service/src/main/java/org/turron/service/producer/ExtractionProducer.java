package org.turron.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.service.entity.SourceEntity;
import org.turron.service.entity.SnippetEntity;
import org.turron.service.event.SnippetFrameExtractedEvent;
import org.turron.service.event.SourceFrameExtractedEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractionProducer {
    private final KafkaTemplate<String, SnippetFrameExtractedEvent> snippetFrameExtractedEventTemplate;
    private final KafkaTemplate<String, SourceFrameExtractedEvent> sourceFrameExtractedEventTemplate;

    public void sendSnippetFrameExtractedEvent(String correlationId, SnippetEntity extractedFrame) {
        SnippetFrameExtractedEvent event = new SnippetFrameExtractedEvent(
                correlationId,
                extractedFrame.getFrameId(),
                extractedFrame.getSnippetId(),
                extractedFrame.getFrameUrl()
        );

        log.info("Sending SnippetFrameExtractedEvent [frameId={}, snippetId={}, imageUrl={}, correlationId={}] to topic 'snippet.frames.extracted'",
                event.getFrameId(),
                event.getSnippetId(),
                event.getFrameUrl(),
                event.getCorrelationId()
        );
        log.debug("Published SnippetFrameExtractedEvent: {}", event);

        snippetFrameExtractedEventTemplate
                .send("snippet.frames.extracted", event)
                .thenAccept(sendResult -> log.info("SnippetFrameExtractedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send SnippetFrameExtractedEvent [frameId={}, correlationId={}]: {}",
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
