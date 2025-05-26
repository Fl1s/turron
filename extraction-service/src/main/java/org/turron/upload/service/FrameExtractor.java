package org.turron.upload.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FrameExtractor {

    public List<File> extractFrames(File videoFile) {
        log.info("Extracting key frames from video file: {}", videoFile.getAbsolutePath());

        List<File> frames = new ArrayList<>();
        File outputDir = new File(System.getProperty("java.io.tmpdir"), "frames_" + System.nanoTime());
        if (!outputDir.mkdirs()) {
            throw new RuntimeException("Failed to create temporary directory: " + outputDir.getAbsolutePath());
        }

        String outputPattern = new File(outputDir, "frame-%02d.png").getAbsolutePath();
        List<String> command = List.of(
                "ffmpeg",
                "-i", videoFile.getAbsolutePath(),
                "-vf", "select='eq(pict_type\\,I)',scale=320:-1",
                "-vsync", "vfr",
                "-q:v", "2",
                outputPattern
        );

        try {
            log.debug("Running ffmpeg command: {}", String.join(" ", command));
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("ffmpeg exited with code {}", exitCode);
                throw new RuntimeException("ffmpeg failed with exit code " + exitCode);
            }

            File[] files = outputDir.listFiles((dir, name) -> name.endsWith(".png"));
            if (files != null) {
                frames.addAll(List.of(files));
            }

            log.info("Successfully extracted {} frames", frames.size());
            return frames;

        } catch (IOException | InterruptedException e) {
            log.error("Failed to extract frames using ffmpeg", e);
            throw new RuntimeException("Frame extraction failed", e);
        }
    }
}


