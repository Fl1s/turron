package org.turron.thought.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.turron.thought.dto.PurgeStatsDto;
import org.turron.thought.entity.ThoughtEntity;
import org.turron.thought.repository.MemoryRepository;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurgeService {
    private final MemoryRepository memoryRepository;
    private final TtlDecayService ttlDecayService;
    @Transactional
    public void manualPurge() {
        Instant now = Instant.now();
        var expired = memoryRepository.findByExpiresAtBefore(now);
        if (expired.isEmpty()) {
            log.info("[Purge] No expired thoughts to delete at {}", now);
            return;
        }
        log.info("[Purge] Deleting {} expired thoughts", expired.size());
        memoryRepository.deleteAll(expired);
    }

    @Transactional
    public void recalculateExpires() {
        var all = memoryRepository.findAll();
        log.info("[Purge] Recalculating expiresAt for {} thoughts", all.size());
        for (ThoughtEntity e : all) {
            Duration ttl = ttlDecayService.calculateTtl(e.getImportance());
            e.setExpiresAt(e.getCreatedAt().plus(ttl));
        }
        memoryRepository.saveAll(all);
    }

    public PurgeStatsDto getPurgeStats() {
        return new PurgeStatsDto(memoryRepository.count(), memoryRepository.countByExpiresAtBefore(Instant.now()));
    }

    @Scheduled(fixedRateString = "${purge.schedule.fixedRate:600000}")
    public void scheduledPurge() {
        manualPurge();
    }
}
