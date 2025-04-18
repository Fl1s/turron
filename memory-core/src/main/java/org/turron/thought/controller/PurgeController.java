package org.turron.thought.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.turron.thought.dto.PurgeStatsDto;
import org.turron.thought.service.PurgeService;

@RestController
@RequestMapping("/api/v1/purge")
@RequiredArgsConstructor
@Slf4j
public class PurgeController {
    private final PurgeService purgeService;

    @PostMapping("/purge/manual")
    public ResponseEntity<Void> manualPurge() {
        log.info("[MemoryController] Manual purge requested");
        purgeService.manualPurge();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/purge/recalculate")
    public ResponseEntity<Void> recalculateExpires() {
        log.info("[MemoryController] TTL recalculation requested");
        purgeService.recalculateExpires();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/purge/stats")
    public ResponseEntity<PurgeStatsDto> purgeStats() {
        log.info("[MemoryController] Purge stats requested");
        return ResponseEntity.ok(purgeService.getPurgeStats());
    }

    @GetMapping("/purge/ping")
    public ResponseEntity<String> purgePing() {
        log.debug("[MemoryController] Purge ping");
        return ResponseEntity.ok("Purge module is alive");
    }
}
