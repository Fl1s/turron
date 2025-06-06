package org.turron.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.turron.service.event.SourceFrameHashedEvent;
import org.turron.service.event.SnippetFrameHashedEvent;
import org.turron.service.service.SearchingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchingListener {

    private final SearchingService searchingService;

    @KafkaListener(topics = "snippet.frames.hashed", groupId = "hashing-service",
            containerFactory = "kafkaListenerContainerFactorySnippetFrameHashed")
    public void onSnippetFrameHashed(SnippetFrameHashedEvent event) {
        log.info("Received snippet.frames.hashed event: {}", event);

        searchingService.storeSnippetHash(event);
    }
    @KafkaListener(topics = "source.frames.hashed", groupId = "hashing-service",
            containerFactory = "kafkaListenerContainerFactorySourceFrameHashed")
    public void onSourceFrameHashed(SourceFrameHashedEvent event) {
        log.info("Received source.frames.hashed event: {}", event);
        searchingService.storeSourceHash(event);
    }
}


