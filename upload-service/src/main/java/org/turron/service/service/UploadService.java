package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.turron.service.dto.VideoDto;
import org.turron.service.entity.VideoEntity;
import org.turron.service.mapper.VideoMapper;
import org.turron.service.producer.UploadProducer;
import org.turron.service.repository.UploadRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final UploadRepository uploadRepository;
    private final UploadProducer uploadProducer;
    private final MinioService minioService;
    private final VideoMapper videoMapper;

    @Transactional
    public VideoDto upload(MultipartFile file) {
        log.info("Starting upload of video: {}", file.getOriginalFilename());

        String videoId = UUID.randomUUID().toString();
        String path = minioService.uploadVideo(videoId, file);

        VideoEntity entity = new VideoEntity();
        entity.setVideoId(videoId);
        entity.setSourceUrl(path);

        VideoEntity saved = uploadRepository.save(entity);
        log.info("Video metadata saved in db with videoId: {}", saved.getVideoId());

        uploadProducer.sendUploadedEvent(saved);
        return videoMapper.toDto(saved);
    }
}