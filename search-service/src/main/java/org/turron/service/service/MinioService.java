package org.turron.service.service;

import io.minio.MinioClient;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;

import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for interacting with MinIO object storage.
 * Responsible for downloading video files by source ID.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    MinioClient minioClient;

    @Value("${minio.buckets.uploads}")
    private String uploadsBucket;

    /**
     * Generates a pre-signed URL for accessing an object in MinIO.
     * <p>
     * The URL provides temporary public access to the specified object using HTTP GET,
     * valid for 1 hour (3600 seconds).
     *
     * @param objectName the name of the object in the bucket
     * @return a pre-signed URL string to access the object
     * @throws RuntimeException if URL generation fails
     */
    public String generatePreSignedUrl(String pathPrefix, String objectName) {
        try {
            String fullObjectPath = pathPrefix +"/"+ objectName;

            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(uploadsBucket)
                    .object(fullObjectPath)
                    .expiry(1, TimeUnit.HOURS)
                    .region("us-east-1")
                    .build();

            return minioClient.getPresignedObjectUrl(args);
        } catch (Exception e) {
            log.error("Failed to generate pre-signed URL for {}", objectName, e);
            throw new RuntimeException("Could not generate pre-signed URL", e);
        }
    }
}