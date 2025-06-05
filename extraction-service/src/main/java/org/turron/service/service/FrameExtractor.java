package org.turron.service.service;

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

        extract(videoFile, outputDir, frames, true);

        if (frames.isEmpty()) {
            log.warn("No I-frames found, falling back to fps-based extraction...");
            extract(videoFile, outputDir, frames, false);
        }

        log.info("Successfully extracted {} frames", frames.size());
        return frames;
    }

    private void extract(File videoFile, File outputDir, List<File> frames, boolean useIFrames) {
        String pattern = new File(outputDir, (useIFrames ? "iframe" : "fallback") + "-%02d.png").getAbsolutePath();
        List<String> command = new ArrayList<>(List.of(
                "ffmpeg",
                "-i", videoFile.getAbsolutePath()
        ));

        if (useIFrames) {
            command.addAll(List.of(
                    "-vf", "select='eq(pict_type\\,I)',scale=320:-1",
                    "-vsync", "vfr"
            ));
        } else {
            command.addAll(List.of(
                    "-vf", "fps=1,scale=320:-1",
                    "-vsync", "vfr"
            ));
        }

        command.addAll(List.of(
                "-q:v", "2",
                pattern
        ));

        try {
            log.debug("Running ffmpeg command: {}", String.join(" ", command));
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("ffmpeg exited with code {}", exitCode);
                return;
            }

            File[] files = outputDir.listFiles((dir, name) -> name.endsWith(".png"));
            if (files != null) {
                frames.addAll(List.of(files));
            }
        } catch (IOException | InterruptedException e) {
            log.error("Failed to extract frames using ffmpeg", e);
        }
    }

}


