package org.turron.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.turron.service.event.FrameHashedEvent;
import org.turron.service.service.SearchingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchingListener {

    private final SearchingService searchingService;

    @KafkaListener(topics = "video.frames.hashed", groupId = "hashing-service",
            containerFactory = "kafkaListenerContainerFactoryFrameHashed")
    public void onFrameHashedEvent(FrameHashedEvent event) {
        log.info("Received video.frames.hashed event: {}", event);

        searchingService.storeHash(event);
        searchingService.findMostSimilarVideo(event);
    }
}


