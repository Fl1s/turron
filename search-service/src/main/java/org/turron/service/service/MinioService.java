package org.turron.service.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Service for interacting with MinIO object storage.
 * Responsible for downloading video files by source ID.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.buckets.uploads}")
    private String uploadsBucket;

    /**
     * Downloads a video file from the MinIO bucket using the given source ID.
     *
     * @param sourceId the ID of the source video to download
     * @return a {@link File} object pointing to a temporary file containing the downloaded video
     * @throws RuntimeException if the video cannot be downloaded
     */
    public File downloadVideo(String sourceId) {
        String objectName = "sources/" + sourceId;
        log.info("Downloading video from MinIO: bucket='{}', object='{}'", uploadsBucket, objectName);

        try {
            File tempFile = File.createTempFile("video-", ".mp4");

            try (InputStream in = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(uploadsBucket)
                    .object(objectName)
                    .build());
                 FileOutputStream out = new FileOutputStream(tempFile)) {

                in.transferTo(out);
                log.info("Video downloaded to temporary file: {}", tempFile.getAbsolutePath());
                return tempFile;
            }
        } catch (Exception e) {
            log.error("Failed to download video from MinIO", e);
            throw new RuntimeException("Failed to download video from MinIO", e);
        }
    }
}