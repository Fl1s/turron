package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.turron.service.repository.SnippetRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanupService {
    private final MinioService minioService;
    private final SnippetRepository snippetRepository;

    @Scheduled(fixedRate = 600_000)
    public void cleanupSnippets() {
        log.info("Starting cleanup for MinIO folder /snippets and postgres table 'snippets'...");

        try {
            minioService.deleteFolder("snippets");
            snippetRepository.deleteAll();

            log.info("Cleanup completed: all records and files removed.");
        } catch (Exception e) {
            log.error("Cleanup failed:", e);
        }
    }
}
