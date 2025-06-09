package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.turron.service.repository.SnippetRepository;

/**
 * Service that periodically cleans up the video repository to remove all stored video entries.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CleanupService {

    private final SnippetRepository snippetRepository;

    /**
     * Scheduled task that deletes all records from the video table every 10 minutes.
     * Logs progress and errors.
     */
    @Scheduled(fixedRate = 600_000)
    public void cleanupVideos() {
        log.info("Starting cleanup for table 'snippets'...");
        try {
            snippetRepository.deleteAll();
            log.info("Cleanup completed: all records and files removed.");
        } catch (Exception e) {
            log.error("Cleanup failed:", e);
        }
    }
}

