package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.turron.service.repository.VideoRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanupService {
    private final VideoRepository videoRepository;

    @Scheduled(fixedRate = 600_000)
    public void cleanupVideos() {
        log.info("Starting cleanup for table 'videos'...");
        try {
            videoRepository.deleteAll();
            log.info("Cleanup completed: all records and files removed.");
        } catch (Exception e) {
            log.error("Cleanup failed:", e);
        }
    }
}
