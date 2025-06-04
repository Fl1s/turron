package org.turron.service.service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    private static @NotNull Process getProcess(File tempFile) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                tempFile.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String durationStr = reader.readLine();
            if (durationStr == null) {
                throw new IllegalArgumentException("Could not determine video duration.");
            }
            double duration = Double.parseDouble(durationStr);
            log.debug("Video duration: {} seconds", duration);
            if (duration > 5.0) {
                throw new IllegalArgumentException("Video is longer than 5 seconds.");
            }
        }

        return process;
    }

    public String uploadVideo(String filename, MultipartFile file) {
        return uploadFile(filename, file, "videos", true);
    }

    public String uploadSource(String filename, MultipartFile file) {
        return uploadFile(filename, file, "sources", false);
    }

    private String uploadFile(String filename, MultipartFile file, String folder, boolean validateDuration) {
        log.info("Uploading file to '{}': originalFilename={}, contentType={}", folder, file.getOriginalFilename(), file.getContentType());

        if (file.isEmpty() || !file.getContentType().startsWith("video/")) {
            log.warn("File is empty or not a video: {}", file.getOriginalFilename());
            throw new IllegalArgumentException("Not a video file.");
        }

        File tempFile = null;
        try {
            tempFile = File.createTempFile("upload-", ".tmp");
            file.transferTo(tempFile);
            log.debug("Temporary file created at {}", tempFile.getAbsolutePath());

            if (validateDuration) {
                validateVideo(tempFile);
            }

            String objectPath = folder + "/" + filename;
            uploadFileToMinio(objectPath, tempFile, file.getContentType());

            log.info("File successfully uploaded to MinIO at path: {}", objectPath);
            return "minio://" + bucketName + "/" + objectPath;

        } catch (IOException e) {
            log.error("Failed to process file", e);
            throw new RuntimeException("Failed to process file.", e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                if (tempFile.delete()) {
                    log.debug("Temporary file deleted: {}", tempFile.getAbsolutePath());
                } else {
                    log.warn("Failed to delete temporary file: {}", tempFile.getAbsolutePath());
                }
            }
        }
    }


    private void validateVideo(File tempFile) {
        try {
            log.debug("Validating video file duration...");
            Process process = getProcess(tempFile);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.warn("ffprobe exited with non-zero status: {}", exitCode);
            }
            log.debug("Video duration validated successfully.");
        } catch (IOException | InterruptedException e) {
            log.error("Video validation failed", e);
            throw new RuntimeException("Video validation failed.", e);
        }
    }

    private void uploadFileToMinio(String filePath, File file, String contentType) {
        try (InputStream in = new FileInputStream(file)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filePath)
                            .stream(in, file.length(), -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to upload file to MinIO at path: {}", filePath, e);
            throw new RuntimeException("Failed to upload file to MinIO: " + e.getMessage(), e);
        }
    }
    public void deleteFolder(String folderPath) {
        try {
            Iterable<Result<Item>> objects = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
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
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );
            }

            log.info("All objects under '{}' have been deleted from bucket '{}'", folderPath, bucketName);
        } catch (Exception e) {
            log.error("Failed to delete folder '{}' from MinIO", folderPath, e);
            throw new RuntimeException(e);
        }
    }
}
