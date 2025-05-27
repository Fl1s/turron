package org.turron.service.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.buckets.frames}")
    private String framesBucket;
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

}
