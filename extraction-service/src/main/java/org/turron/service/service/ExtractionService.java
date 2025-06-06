package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.turron.service.entity.SourceEntity;
import org.turron.service.entity.SnippetEntity;
import org.turron.service.event.SourceUploadedEvent;
import org.turron.service.event.SnippetUploadedEvent;
import org.turron.service.producer.ExtractionProducer;
import org.turron.service.repository.SourceRepository;
import org.turron.service.repository.SnippetRepository;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractionService {

    private final MinioService minioService;
    private final SnippetRepository snippetRepository;
    private final SourceRepository sourceRepository;
    private final FrameExtractor frameExtractor;
    private final ExtractionProducer producer;

    public void extractFramesFromSnippet(SnippetUploadedEvent event) {
        MDC.put("correlationId", event.getCorrelationId());
        log.info("Starting frame extraction for snippet: {}", event.getSnippetId());

        File tempVideo = minioService.downloadVideo(event.getSourceUrl());
        List<File> frames = frameExtractor.extractFrames(tempVideo);
        log.info("Extracted {} key frames from snippet {}", frames.size(), event.getSnippetId());

        for (int i = 0; i < frames.size(); i++) {
            String frameId = event.getSnippetId() + "-frame-" + String.format("%02d", i + 1);
            minioService.uploadFrame(event.getSnippetId(), frames.get(i), i + 1, false);

            SnippetEntity entity = new SnippetEntity();
            entity.setFrameId(frameId);
            entity.setSnippetId(event.getSnippetId());
            entity.setFrameUrl("minio://frames/snippets/" + event.getSnippetId() + "/" + (i + 1) + ".png");

            SnippetEntity saved = snippetRepository.save(entity);
            producer.sendSnippetFrameExtractedEvent(event.getCorrelationId(), saved);
        }

        cleanup(tempVideo, frames);
        log.info("Frame extraction completed for snippet: {}", event.getSnippetId());
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

