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
import java.util.concurrent.TimeUnit;
import io.minio.http.Method;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Service for interacting with MinIO: uploading, validating, and deleting snippet/source files.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    /**
     * Creates an ffprobe process to determine video duration.
     *
     * @param tempFile the temporary video file
     * @return the created {@link Process}
     * @throws IOException if the process cannot be started
     * @throws IllegalArgumentException if duration cannot be determined or exceeds 5 seconds
     */
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

    /**
     * Uploads a snippet video file and validates its duration (must not exceed 5 seconds).
     *
     * @param filename the target object name
     * @param file the video file as a {@link MultipartFile}
     * @return the MinIO URL of the uploaded file
     * @throws RuntimeException if validation or upload fails
     */
    public String uploadSnippet(String filename, MultipartFile file) {
        return uploadFile(filename, file, "snippets", true);
    }

    /**
     * Uploads a source video file without validating its duration.
     *
     * @param filename the target object name
     * @param file the source file as a {@link MultipartFile}
     * @return the MinIO URL of the uploaded file
     * @throws RuntimeException if upload fails
     */
    public String uploadSource(String filename, MultipartFile file) {
        return uploadFile(filename, file, "sources", false);
    }

    /**
     * Generates a temporary presigned URL for accessing an object in the MinIO bucket.
     *
     * @param objectPath      the path to the object inside the bucket (e.g., "snippets/uuid.mp4")
     * @param durationInDays  the validity period of the URL, in days (maximum 7)
     * @return the generated presigned URL as a {@link String}, valid for the specified duration
     * @throws RuntimeException if URL generation fails or parameters are invalid
     */
    public String generatePresignedUrl(String objectPath, int durationInDays) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .method(Method.GET)
                            .expiry((int) TimeUnit.DAYS.toSeconds(durationInDays))
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to generate presigned URL for: {}", objectPath, e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    /**
     * Core method for uploading a file to MinIO.
     *
     * @param filename the target object name
     * @param file the uploaded file
     * @param folder the folder path prefix in the bucket
     * @param validateDuration whether to validate video duration
     * @return the MinIO URL of the uploaded file
     * @throws RuntimeException if upload fails
     */
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
            return generatePresignedUrl(objectPath, 7);

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


    /**
     * Validates the duration of a video file using ffprobe.
     *
     * @param tempFile the temporary file containing the video
     * @throws RuntimeException if duration exceeds the limit or validation fails
     */
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

    /**
     * Uploads a file to MinIO under the given path.
     *
     * @param filePath target object path in the bucket
     * @param file the file to upload
     * @param contentType the MIME type of the file
     * @throws RuntimeException if the upload fails
     */
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

    /**
     * Deletes all objects under the specified prefix (simulating folder deletion).
     *
     * @param folderPath prefix path to delete
     * @throws RuntimeException if deletion fails
     */
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
