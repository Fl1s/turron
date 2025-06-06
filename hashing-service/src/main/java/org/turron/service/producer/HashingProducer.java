package org.turron.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.service.entity.SourceEntity;
import org.turron.service.entity.SnippetEntity;
import org.turron.service.event.SourceFrameHashedEvent;
import org.turron.service.event.SnippetFrameHashedEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashingProducer {
    private final KafkaTemplate<String, SnippetFrameHashedEvent> snippetHashedEventTemplate;
    private final KafkaTemplate<String, SourceFrameHashedEvent> sourceHashedEventTemplate;

    public void sendSnippetFramesHashedEvent(String correlationId, SnippetEntity entity) {
        SnippetFrameHashedEvent event = new SnippetFrameHashedEvent(
                entity.getHashId(),
                entity.getSnippetId(),
                entity.getFrameId(),
                entity.getFrameHash(),
                correlationId
        );

        log.info("Sending SnippetFrameHashedEvent [hashId={}, snippetId={}, frameId={}, frameHash={}, correlationId={}] to topic 'snippet.frames.hashed'",
                event.getHashId(),
                event.getSnippetId(),
                event.getFrameId(),
                event.getFrameHash(),
                event.getCorrelationId()
        );

        snippetHashedEventTemplate
                .send("snippet.frames.hashed", event)
                .thenAccept(sendResult -> log.info("SnippetFrameHashedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send SnippetFrameHashedEvent [snippetId={}, correlationId={}]: {}",
                            event.getSnippetId(), event.getCorrelationId(), ex.getMessage(), ex);
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
