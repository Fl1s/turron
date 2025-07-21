package org.turron.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.service.entity.SnippetEntity;
import org.turron.service.entity.SourceEntity;
import org.turron.service.event.SnippetUploadedEvent;
import org.turron.service.event.SourceUploadedEvent;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadProducer {

    private final KafkaTemplate<String, SnippetUploadedEvent> snippetUploadedEventTemplate;
    private final KafkaTemplate<String, SourceUploadedEvent> sourceUploadedEventTemplate;

    public void sendSnippetUploadedEvent(SnippetEntity snippetEntity) {
        String correlationId = UUID.randomUUID().toString();

        SnippetUploadedEvent event = new SnippetUploadedEvent(
                correlationId,
                snippetEntity.getSnippetId(),
                snippetEntity.getSourceUrl()
        );

        log.info("Sending SnippetUploadedEvent [snippetId={}, sourceUrl={}, correlationId={}] to topic 'snippet.uploaded'",
                event.getSnippetId(),
                event.getSourceUrl(),
                correlationId
        );

        snippetUploadedEventTemplate
                .send("snippet.uploaded", event.getSnippetId(), event)
                .thenAccept(sendResult -> log.info("SnippetUploadedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send SnippetUploadedEvent [snippetId={}, correlationId={}]: {}",
                            event.getSnippetId(), correlationId, ex.getMessage(), ex);
                    return null;
                });
    }
    public void sendSourceUploadedEvent(SourceEntity sourceEntity) {
        String correlationId = UUID.randomUUID().toString();

        SourceUploadedEvent event = new SourceUploadedEvent(
                correlationId,
                sourceEntity.getSourceId(),
                sourceEntity.getSourceUrl()
        );

        log.info("Sending SourceUploadedEvent [sourceId={}, sourceUrl={}, correlationId={}] to topic 'source.uploaded'",
                event.getSourceId(),
                event.getSourceUrl(),
                correlationId
        );

        sourceUploadedEventTemplate
                .send("source.uploaded", event.getSourceId(), event)
                .thenAccept(sendResult -> log.info("SourceUploadedEvent successfully sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Failed to send SourceUploadedEvent [sourceId={}, correlationId={}]: {}",
                            event.getSourceId(), correlationId, ex.getMessage(), ex);
                    return null;
                });
    }
}
