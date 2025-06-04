package org.turron.service.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.buckets.uploads}")
    private String uploadsBucket;

    @Value("${minio.buckets.frames}")
    private String framesBucket;

    public File downloadVideo(String sourceUrl) {
        try {
            String prefix = "minio://" + uploadsBucket + "/";
            if (!sourceUrl.startsWith(prefix)) {
                throw new IllegalArgumentException("sourceUrl does not start with expected prefix: " + prefix);
            }
            String path = sourceUrl.replace(prefix, "");
            log.info("Downloading video from MinIO: {}", path);

            File tempFile = File.createTempFile("video-", ".mp4");
            try (InputStream in = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(uploadsBucket)
                    .object(path)
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

    public void uploadFrame(String id, File frameFile, int frameNumber, boolean isSource) {
        String folder = isSource ? "sources" : "videos";
        String objectName = String.format("%s/%s/%d.png", folder, id, frameNumber);
        log.info("Uploading frame to MinIO: bucket={}, objectName={}", framesBucket, objectName);

        try (InputStream inputStream = new FileInputStream(frameFile)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(framesBucket)
                            .object(objectName)
                            .stream(inputStream, frameFile.length(), -1)
                            .contentType("image/png")
                            .build()
            );
            log.info("Successfully uploaded frame {} for {} {}", frameNumber, folder, id);
        } catch (Exception e) {
            log.error("Failed to upload frame {} for {} {} to MinIO", frameNumber, folder, id, e);
            throw new RuntimeException("Failed to upload frame to MinIO: " + e.getMessage(), e);
        }
    }
}

