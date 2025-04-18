package org.turron.thought.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.turron.thought.dto.ThoughtDto;
import org.turron.thought.service.MemoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/memory")
@RequiredArgsConstructor
@Slf4j
public class MemoryController {
    private final MemoryService memoryService;

    @GetMapping("/{thoughtId}")
    public ResponseEntity<ThoughtDto> getThought(@PathVariable String thoughtId) {
        log.debug("[MemoryController] Fetching thought with ID={}", thoughtId);
        return memoryService.getThought(thoughtId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ThoughtDto>> searchThoughts(
            @RequestParam List<String> tags,
            @RequestParam String type,
            @RequestParam Integer importance) {
        log.debug("[MemoryController] Searching thoughts tags={}, type={}, importance={}", tags, type, importance);
        var list = memoryService.searchThoughts(tags, type, importance);
        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    @DeleteMapping("/forget/{thoughtId}")
    public ResponseEntity<Void> forgetThought(@PathVariable String thoughtId) {
        log.info("[MemoryController] Forgetting thought with ID={}", thoughtId);
        memoryService.forgetThought(thoughtId);
        return ResponseEntity.ok().build();
    }
}
