package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.turron.service.entity.SourceEntity;
import org.turron.service.entity.VideoEntity;
import org.turron.service.event.SourceFrameExtractedEvent;
import org.turron.service.event.VideoFrameExtractedEvent;
import org.turron.service.producer.HashingProducer;
import org.turron.service.repository.SourceRepository;
import org.turron.service.repository.VideoRepository;

import java.awt.image.BufferedImage;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashingService {
    private final VideoRepository videoRepository;
    private final SourceRepository sourceRepository;
    private final HashingProducer hashingProducer;
    private final MinioService minioService;

    public void videoFrameHash(VideoFrameExtractedEvent event) {
        try {
            BufferedImage image = minioService.downloadImage(event.getFrameUrl());

            String hash = PerceptualHash.compute(image);
            String hashId = UUID.randomUUID().toString();

            VideoEntity entity = new VideoEntity();

            entity.setHashId(hashId);
            entity.setVideoId(event.getVideoId());
            entity.setFrameId(event.getFrameId());
            entity.setFrameUrl(event.getFrameUrl());
            entity.setFrameHash(hash);

            videoRepository.save(entity);
            hashingProducer.sendVideoFramesHashedEvent(event.getCorrelationId(), entity);
            log.info("[Video] Frame hashed and saved: {}", entity);
        } catch (Exception e) {
            log.error("Failed to process frame: {}", event, e);
        }
    }
    public void sourceFrameHash(SourceFrameExtractedEvent event) {
        try {
            BufferedImage image = minioService.downloadImage(event.getFrameUrl());

            String hash = PerceptualHash.compute(image);
            String hashId = UUID.randomUUID().toString();

            SourceEntity entity = new SourceEntity();

            entity.setHashId(hashId);
            entity.setSourceId(event.getSourceId());
            entity.setFrameId(event.getFrameId());
            entity.setFrameUrl(event.getFrameUrl());
            entity.setFrameHash(hash);

            sourceRepository.save(entity);
            hashingProducer.sendSourceFramesHashedEvent(event.getCorrelationId(), entity);
            log.info("[Source] Frame hashed and saved: {}", entity);
        } catch (Exception e) {
            log.error("Failed to process frame: {}", event, e);
        }
    }
}

