package org.turron.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.turron.service.event.SourceFrameExtractedEvent;
import org.turron.service.event.SnippetFrameExtractedEvent;
import org.turron.service.service.HashingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashingListener {
    private final HashingService hashingService;

    @KafkaListener(topics = "snippet.frames.extracted", groupId = "extraction-service",
            containerFactory = "kafkaListenerContainerFactorySnippetFrameExtracted")
    public void onSnippetFrameExtracted(SnippetFrameExtractedEvent event) {
        log.info("Received snippet.frames.extracted event: {}", event);
        hashingService.snippetFrameHash(event);
    }

    @KafkaListener(topics = "source.frames.extracted", groupId = "extraction-service",
            containerFactory = "kafkaListenerContainerFactorySourceFrameExtracted")
    public void onSourceFrameExtracted(SourceFrameExtractedEvent event) {
        log.info("Received source.frames.extracted event: {}", event);
        hashingService.sourceFrameHash(event);
    }
}

