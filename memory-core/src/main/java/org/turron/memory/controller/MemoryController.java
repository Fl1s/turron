package org.turron.memory.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.turron.memory.dto.ThoughtDto;
import org.turron.memory.service.MemoryService;

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
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ThoughtDto>> searchThoughts(
            @RequestParam(value = "tags") List<String> tags,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "importance") Integer importance) {
        log.debug("[MemoryController] Searching thoughts with tags={}, type={}, importance={}", tags, type, importance);
        List<ThoughtDto> thoughts = memoryService.searchThoughts(tags, type, importance);

        return thoughts.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(thoughts);
    }

    @DeleteMapping("/forget/{thoughtId}")
    public ResponseEntity<String> forgetThought(@PathVariable String thoughtId) {
        log.info("[MemoryController] Forgetting thought with ID={}", thoughtId);
        memoryService.forgetThought(thoughtId);
        return ResponseEntity.ok().build();
    }
}
