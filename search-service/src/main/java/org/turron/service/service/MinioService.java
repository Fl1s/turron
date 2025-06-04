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

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.buckets.uploads}")
    private String uploadsBucket;

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

