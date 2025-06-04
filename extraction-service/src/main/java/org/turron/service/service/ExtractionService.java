package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.turron.service.entity.SourceEntity;
import org.turron.service.entity.VideoEntity;
import org.turron.service.event.SourceUploadedEvent;
import org.turron.service.event.VideoUploadedEvent;
import org.turron.service.producer.ExtractionProducer;
import org.turron.service.repository.SourceRepository;
import org.turron.service.repository.VideoRepository;

import javax.xml.transform.Source;
import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractionService {

    private final MinioService minioService;
    private final VideoRepository videoRepository;
    private final SourceRepository sourceRepository;
    private final FrameExtractor frameExtractor;
    private final ExtractionProducer producer;

    public void extractFramesFromVideo(VideoUploadedEvent event) {
        MDC.put("correlationId", event.getCorrelationId());
        log.info("Starting frame extraction for video: {}", event.getVideoId());

        File tempVideo = minioService.downloadVideo(event.getSourceUrl());
        List<File> frames = frameExtractor.extractFrames(tempVideo);
        log.info("Extracted {} key frames from video {}", frames.size(), event.getVideoId());

        for (int i = 0; i < frames.size(); i++) {
            String frameId = event.getVideoId() + "-frame-" + String.format("%02d", i + 1);
            minioService.uploadFrame(event.getVideoId(), frames.get(i), i + 1, false);

            VideoEntity entity = new VideoEntity();
            entity.setFrameId(frameId);
            entity.setVideoId(event.getVideoId());
            entity.setFrameUrl("minio://frames/videos/" + event.getVideoId() + "/" + (i + 1) + ".png");

            VideoEntity saved = videoRepository.save(entity);
            producer.sendVideoFrameExtractedEvent(event.getCorrelationId(), saved);
        }

        cleanup(tempVideo, frames);
        log.info("Frame extraction completed for video: {}", event.getVideoId());
        MDC.clear();
    }

    public void extractFramesFromSource(SourceUploadedEvent event) {
        MDC.put("correlationId", event.getCorrelationId());
        log.info("Starting frame extraction for source: {}", event.getSourceId());

        File tempVideo = minioService.downloadVideo(event.getSourceUrl());
        List<File> frames = frameExtractor.extractFrames(tempVideo);
        log.info("Extracted {} key frames from source {}", frames.size(), event.getSourceId());

        for (int i = 0; i < frames.size(); i++) {
            String frameId = event.getSourceId() + "-frame-" + String.format("%02d", i + 1);
            minioService.uploadFrame(event.getSourceId(), frames.get(i), i + 1, true);

            SourceEntity entity = new SourceEntity();
            entity.setFrameId(frameId);
            entity.setSourceId(event.getSourceId());
            entity.setFrameUrl("minio://frames/sources/" + event.getSourceId() + "/" + (i + 1) + ".png");

            SourceEntity saved = sourceRepository.save(entity);
            producer.sendSourceFrameExtractedEvent(event.getCorrelationId(), saved);
        }

        cleanup(tempVideo, frames);
        log.info("Frame extraction completed for source: {}", event.getSourceId());
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

