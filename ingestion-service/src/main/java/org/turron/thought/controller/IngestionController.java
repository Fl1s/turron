package org.turron.thought.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.turron.thought.config.SagaOrchestrator;
import org.turron.thought.dto.ThoughtDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;
@RestController
@RequestMapping("/api/v1/ingest")
@RequiredArgsConstructor
@Slf4j
public class IngestionController {
    private final SagaOrchestrator sagaOrchestrator;

    @PostMapping
    @Async
    public CompletableFuture<ResponseEntity<String>> createThought(@RequestBody ThoughtDto thoughtDto) {
        log.info("[IngestionController] Received request to create single thought.");
        sagaOrchestrator.startSaga(thoughtDto);
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.CREATED).body("Saga started: Thought processing initiated")
        );
    }

    @Async
    @PostMapping("/bulk")
    public CompletableFuture<ResponseEntity<String>> createThoughtBulk(@RequestBody List<ThoughtDto> thoughtDtos) {
        log.info("[IngestionController] Received bulk request. Size={}", thoughtDtos.size());
        thoughtDtos.forEach(sagaOrchestrator::startSaga);
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.CREATED).body("Saga started: Thought bulk processing initiated")
        );
    }

    @GetMapping("/ping")
    public ResponseEntity<String> pingThought() {
        log.debug("[IngestionController] Ping endpoint called.");
        return ResponseEntity.ok().body("Thought ping");
    }
}

