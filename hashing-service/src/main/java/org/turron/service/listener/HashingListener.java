package org.turron.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.turron.service.event.SourceFrameExtractedEvent;
import org.turron.service.event.VideoFrameExtractedEvent;
import org.turron.service.service.HashingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashingListener {
    private final HashingService hashingService;

    @KafkaListener(topics = "video.frames.extracted", groupId = "extraction-service",
            containerFactory = "kafkaListenerContainerFactoryVideoFrameExtracted")
    public void onVideoFrameExtracted(VideoFrameExtractedEvent event) {
        log.info("Received video.frames.extracted event: {}", event);
        hashingService.videoFrameHash(event);
    }

    @KafkaListener(topics = "source.frames.extracted", groupId = "extraction-service",
            containerFactory = "kafkaListenerContainerFactorySourceFrameExtracted")
    public void onSourceFrameExtracted(SourceFrameExtractedEvent event) {
        log.info("Received source.frames.extracted event: {}", event);
        hashingService.sourceFrameHash(event);
    }
}

