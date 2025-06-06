package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.turron.service.dto.SourceDto;
import org.turron.service.dto.SnippetDto;
import org.turron.service.entity.SnippetEntity;
import org.turron.service.entity.SourceEntity;
import org.turron.service.mapper.SourceMapper;
import org.turron.service.mapper.SnippetMapper;
import org.turron.service.producer.UploadProducer;
import org.turron.service.repository.SourceRepository;
import org.turron.service.repository.SnippetRepository;

import java.util.UUID;
/**
 * Service responsible for handling snippet and source file uploads.
 * Performs MinIO upload, entity persistence, and event dispatch.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final SnippetRepository snippetRepository;
    private final SourceRepository sourceRepository;
    private final UploadProducer uploadProducer;
    private final MinioService minioService;
    private final SnippetMapper snippetMapper;
    private final SourceMapper sourceMapper;

    /**
     * Uploads a snippet file, stores its metadata, and publishes a "snippet.uploaded" event.
     *
     * @param file the snippet file to upload
     * @return the uploaded snippet metadata as {@link SnippetDto}
     * @throws RuntimeException if upload or persistence fails
     */
    @Transactional
    public SnippetDto uploadSnippet(MultipartFile file) {
        log.info("Starting upload of snippet: {}", file.getOriginalFilename());

        String snippetId = UUID.randomUUID().toString();
        String path = minioService.uploadSnippet(snippetId, file);

        SnippetEntity entity = new SnippetEntity();
        entity.setSnippetId(snippetId);
        entity.setSourceUrl(path);

        SnippetEntity saved = snippetRepository.save(entity);
        log.info("Snippet metadata saved in db with snippetId: {}", saved.getSnippetId());

        uploadProducer.sendSnippetUploadedEvent(saved);
        return snippetMapper.toDto(saved);
    }
    /**
     * Uploads a source file, stores its metadata, and publishes a "source.uploaded" event.
     *
     * @param file the source file to upload
     * @return the uploaded source metadata as {@link SourceDto}
     * @throws RuntimeException if upload or persistence fails
     */
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