package org.turron.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.turron.service.event.SourceUploadedEvent;
import org.turron.service.event.SnippetUploadedEvent;
import org.turron.service.service.ExtractionService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractionListener {
    private final ExtractionService extractionService;

    @KafkaListener(topics = "snippet.uploaded", groupId = "upload-service",
            containerFactory = "kafkaListenerContainerFactorySnippetUploaded")
    public void onSnippetUploaded(SnippetUploadedEvent event) {
        log.info("Received snippet.uploaded event: {}", event);
        extractionService.extractFramesFromSnippet(event);
    }
    @KafkaListener(topics = "source.uploaded", groupId = "upload-service",
            containerFactory = "kafkaListenerContainerFactorySourceUploaded")
    public void onSourceUploaded(SourceUploadedEvent event) {
        log.info("Received source.uploaded event: {}", event);
        extractionService.extractFramesFromSource(event);
    }
}
