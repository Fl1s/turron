package org.turron.service.service;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Handles interaction with MinIO: downloading images and deleting folders.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.buckets.frames}")
    private String framesBucket;

    /**
     * Downloads an image from MinIO using the specified `minio://bucket/object-path` format.
     *
     * @param sourceUrl the full MinIO URL of the image
     * @return a buffered image object
     * @throws RuntimeException if the image cannot be read or retrieved
     */
    public BufferedImage downloadImage(String sourceUrl) {
        try {
            String prefix = "minio://" + framesBucket + "/";
            if (!sourceUrl.startsWith(prefix)) {
                throw new IllegalArgumentException("sourceUrl does not start with expected prefix: " + prefix);
            }

            String objectPath = sourceUrl.replace(prefix, "");
            log.info("Downloading image from MinIO: {}", objectPath);

            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(framesBucket)
                            .object(objectPath)
                            .build()
            )) {
                BufferedImage image = ImageIO.read(inputStream);
                if (image == null) {
                    throw new IOException("ImageIO.read returned null for: " + sourceUrl);
                }
                return image;
            }

        } catch (Exception e) {
            log.error("Failed to download image from MinIO", e);
            throw new RuntimeException("Failed to download image from MinIO", e);
        }
    }

    /**
     * Deletes all objects under a given folder path in the MinIO bucket.
     *
     * @param folderPath the folder prefix to delete (e.g., "snippets")
     * @throws RuntimeException if deletion fails
     */
    public void deleteFolder(String folderPath) {
        try {
            Iterable<Result<Item>> objects = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(framesBucket)
                            .prefix(folderPath + "/")
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : objects) {
                Item item = result.get();
                String objectName = item.objectName();
                log.debug("Deleting object: {}", objectName);

                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(framesBucket)
                                .object(objectName)
                                .build()
                );
            }

            log.info("All objects under '{}' have been deleted from bucket '{}'", folderPath, framesBucket);
        } catch (Exception e) {
            log.error("Failed to delete folder '{}' from MinIO", folderPath, e);
            throw new RuntimeException(e);
        }
    }
}