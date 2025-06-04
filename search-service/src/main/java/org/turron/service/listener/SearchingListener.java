package org.turron.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.turron.service.event.SourceFrameHashedEvent;
import org.turron.service.event.VideoFrameHashedEvent;
import org.turron.service.service.SearchingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchingListener {

    private final SearchingService searchingService;

    @KafkaListener(topics = "video.frames.hashed", groupId = "hashing-service",
            containerFactory = "kafkaListenerContainerFactoryVideoFrameHashed")
    public void onVideoFrameHashed(VideoFrameHashedEvent event) {
        log.info("Received video.frames.hashed event: {}", event);

        searchingService.storeVideoHash(event);
    }
    @KafkaListener(topics = "source.frames.hashed", groupId = "hashing-service",
            containerFactory = "kafkaListenerContainerFactorySourceFrameHashed")
    public void onSourceFrameHashed(SourceFrameHashedEvent event) {
        log.info("Received source.frames.hashed event: {}", event);
        searchingService.storeSourceHash(event);
    }
}


