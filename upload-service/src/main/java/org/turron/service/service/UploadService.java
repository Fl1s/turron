package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.turron.service.dto.SourceDto;
import org.turron.service.dto.VideoDto;
import org.turron.service.entity.SourceEntity;
import org.turron.service.entity.VideoEntity;
import org.turron.service.mapper.SourceMapper;
import org.turron.service.mapper.VideoMapper;
import org.turron.service.producer.UploadProducer;
import org.turron.service.repository.SourceRepository;
import org.turron.service.repository.VideoRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final VideoRepository videoRepository;
    private final SourceRepository sourceRepository;
    private final UploadProducer uploadProducer;
    private final MinioService minioService;
    private final VideoMapper videoMapper;
    private final SourceMapper sourceMapper;

    @Transactional
    public VideoDto uploadVideo(MultipartFile file) {
        log.info("Starting upload of video: {}", file.getOriginalFilename());

        String videoId = UUID.randomUUID().toString();
        String path = minioService.uploadVideo(videoId, file);

        VideoEntity entity = new VideoEntity();
        entity.setVideoId(videoId);
        entity.setSourceUrl(path);

        VideoEntity saved = videoRepository.save(entity);
        log.info("Video metadata saved in db with videoId: {}", saved.getVideoId());

        uploadProducer.sendVideoUploadedEvent(saved);
        return videoMapper.toDto(saved);
    }
    @Transactional
    public SourceDto uploadSource(MultipartFile file) {
        log.info("Starting upload of source: {}", file.getOriginalFilename());

        String sourceId = UUID.randomUUID().toString();
        String path = minioService.uploadSource(sourceId, file);

        SourceEntity entity = new SourceEntity();
        entity.setSourceId(sourceId);
        entity.setSourceUrl(path);

        SourceEntity saved = sourceRepository.save(entity);
        log.info("Source metadata saved in db with sourceId: {}", saved.getSourceId());

        uploadProducer.sendSourceUploadedEvent(saved);
        return sourceMapper.toDto(saved);
    }
}