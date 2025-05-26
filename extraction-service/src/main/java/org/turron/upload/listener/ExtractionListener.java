package org.turron.upload.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.turron.upload.event.VideoUploadedEvent;
import org.turron.upload.service.ExtractionService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractionListener {
    private final ExtractionService extractionService;

    @KafkaListener(topics = "video.uploaded", groupId = "upload-service",
            containerFactory = "kafkaListenerContainerFactoryVideoUploaded")
    public void finalizeRegistration(VideoUploadedEvent event) {
        log.info("Received video.uploaded event: {}", event);
        extractionService.extractFramesFromVideo(event);
    }
}
