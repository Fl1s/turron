package org.turron.upload.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
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

    public String uploadVideo(String filename, MultipartFile videoFile) {
        log.info("Uploading video: originalFilename={}, contentType={}", videoFile.getOriginalFilename(), videoFile.getContentType());

        if (videoFile.isEmpty() || !videoFile.getContentType().startsWith("video/")) {
            log.warn("File is empty or not a video: {}", videoFile.getOriginalFilename());
            throw new IllegalArgumentException("Not a video file.");
        }

        File tempFile = null;
        try {
            tempFile = File.createTempFile("upload-", ".tmp");
            videoFile.transferTo(tempFile);
            log.debug("Temporary file created at {}", tempFile.getAbsolutePath());

            validateVideo(tempFile);

            String path = "videos/" + filename;
            uploadFileToMinio(path, tempFile, videoFile.getContentType());

            log.info("Video successfully uploaded to MinIO at path: {}", path);
            return "/" + bucketName + "/" + path;

        } catch (IOException e) {
            log.error("Failed to process video file", e);
            throw new RuntimeException("Failed to process video file.", e);
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
}
