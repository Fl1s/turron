package org.turron.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.turron.service.event.FrameExtractedEvent;
import org.turron.service.service.HashingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashingListener {
    private final HashingService hashingService;

    @KafkaListener(topics = "video.frames.extracted", groupId = "extraction-service",
            containerFactory = "kafkaListenerContainerFactoryFrameExtracted")
    public void finalizeRegistration(FrameExtractedEvent event) {
        log.info("Received video.uploaded event: {}", event);
        hashingService.frameHash(event);
    }
}

