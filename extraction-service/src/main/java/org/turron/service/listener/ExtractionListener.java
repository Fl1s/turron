package org.turron.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.turron.service.event.SourceUploadedEvent;
import org.turron.service.event.VideoUploadedEvent;
import org.turron.service.service.ExtractionService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractionListener {
    private final ExtractionService extractionService;

    @KafkaListener(topics = "video.uploaded", groupId = "upload-service",
            containerFactory = "kafkaListenerContainerFactoryVideoUploaded")
    public void onVideoUploaded(VideoUploadedEvent event) {
        log.info("Received video.uploaded event: {}", event);
        extractionService.extractFramesFromVideo(event);
    }
    @KafkaListener(topics = "source.uploaded", groupId = "upload-service",
            containerFactory = "kafkaListenerContainerFactorySourceUploaded")
    public void onSourceUploaded(SourceUploadedEvent event) {
        log.info("Received source.uploaded event: {}", event);
        extractionService.extractFramesFromSource(event);
    }
}
