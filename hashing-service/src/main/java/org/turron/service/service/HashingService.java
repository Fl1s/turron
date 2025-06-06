package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.turron.service.entity.SourceEntity;
import org.turron.service.entity.SnippetEntity;
import org.turron.service.event.SourceFrameExtractedEvent;
import org.turron.service.event.SnippetFrameExtractedEvent;
import org.turron.service.producer.HashingProducer;
import org.turron.service.repository.SourceRepository;
import org.turron.service.repository.SnippetRepository;

import java.awt.image.BufferedImage;
import java.util.UUID;

/**
 * Processes extracted snippet and source frames by computing perceptual hashes
 * and saving the results to the database. After hashing, it notifies downstream services.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HashingService {

    private final SnippetRepository snippetRepository;
    private final SourceRepository sourceRepository;
    private final HashingProducer hashingProducer;
    private final MinioService minioService;

    /**
     * Computes and stores the perceptual hash of a snippet frame.
     * Sends a `SnippetFramesHashedEvent` after successful processing.
     *
     * @param event the event containing metadata about the snippet frame
     */
    public void snippetFrameHash(SnippetFrameExtractedEvent event) {
        try {
            BufferedImage image = minioService.downloadImage(event.getFrameUrl());
            String hash = PerceptualHash.compute(image);
            String hashId = UUID.randomUUID().toString();

            SnippetEntity entity = new SnippetEntity();
            entity.setHashId(hashId);
            entity.setSnippetId(event.getSnippetId());
            entity.setFrameId(event.getFrameId());
            entity.setFrameUrl(event.getFrameUrl());
            entity.setFrameHash(hash);

            snippetRepository.save(entity);
            hashingProducer.sendSnippetFramesHashedEvent(event.getCorrelationId(), entity);
            log.info("[Snippet] Frame hashed and saved: {}", entity);
        } catch (Exception e) {
            log.error("Failed to process frame: {}", event, e);
        }
    }

    /**
     * Computes and stores the perceptual hash of a source frame.
     * Sends a `SourceFramesHashedEvent` after successful processing.
     *
     * @param event the event containing metadata about the source frame
     */
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