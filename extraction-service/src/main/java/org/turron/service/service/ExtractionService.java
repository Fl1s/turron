package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.turron.service.entity.FrameEntity;
import org.turron.service.event.FrameExtractedEvent;
import org.turron.service.event.VideoUploadedEvent;
import org.turron.service.producer.ExtractionProducer;
import org.turron.service.repository.FrameRepository;

import java.awt.*;
import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractionService {

    private final MinioService minioService;
    private final FrameExtractor frameExtractor;
    private final ExtractionProducer producer;
    private final FrameRepository frameRepository;

    public void extractFramesFromVideo(VideoUploadedEvent event) {
        MDC.put("correlationId", event.getCorrelationId());
        log.info("Starting frame extraction for video: {}", event.getVideoId());

        File tempVideo = minioService.downloadVideo(event.getSourceUrl());

        List<File> frames = frameExtractor.extractFrames(tempVideo);
        log.info("Extracted {} key frames from video {}", frames.size(), event.getVideoId());

        for (int i = 0; i < frames.size(); i++) {
            String frameId = event.getVideoId() + "-frame-" + String.format("%02d", i + 1);
            minioService.uploadFrame(event.getVideoId(), frames.get(i), i + 1);

            FrameEntity entity = new FrameEntity();
            entity.setFrameId(frameId);
            entity.setVideoId(event.getVideoId());
            entity.setFrameUrl("minio://frames/" + event.getVideoId() + "/" + (i + 1) + ".png");

            FrameEntity saved = frameRepository.save(entity);

            producer.sendFramesExtractedEvent(event.getCorrelationId(), saved);
        }

        cleanup(tempVideo, frames);
        log.info("Frame extraction completed for video: {}", event.getVideoId());
        MDC.clear();
    }

    private void cleanup(File video, List<File> frames) {
        if (video != null && video.exists() && video.delete()) {
            log.debug("Temporary video file deleted: {}", video.getAbsolutePath());
        }
        for (File frame : frames) {
            if (frame.exists() && frame.delete()) {
                log.debug("Temporary frame deleted: {}", frame.getAbsolutePath());
            }
        }
    }
}

