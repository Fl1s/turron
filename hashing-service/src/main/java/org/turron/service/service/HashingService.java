package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.turron.service.entity.HashEntity;
import org.turron.service.event.FrameExtractedEvent;
import org.turron.service.producer.HashingProducer;
import org.turron.service.repository.HashRepository;

import java.awt.image.BufferedImage;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashingService {
    private final HashRepository hashRepository;
    private final HashingProducer hashingProducer;
    private final MinioService minioService;

    public void frameHash(FrameExtractedEvent event) {
        try {
            BufferedImage image = minioService.downloadImage(event.getFrameUrl());

            String hash = PerceptualHash.compute(image);
            String hashId = UUID.randomUUID().toString();

            HashEntity entity = new HashEntity(
                    hashId,
                    event.getVideoId(),
                    event.getFrameId(),
                    event.getFrameUrl(),
                    hash
            );

            hashRepository.save(entity);
            hashingProducer.sendFramesHashedEvent(event.getCorrelationId(), entity);
            log.info("Frame hashed and saved: {}", entity);
        } catch (Exception e) {
            log.error("Failed to process frame: {}", event, e);
        }
    }
}

