package org.turron.ingestion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.turron.ingestion.dto.ThoughtDto;
import org.turron.ingestion.mapper.ThoughtEventMapper;
import org.turron.ingestion.producer.ThoughtEventProducer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
@RestController
@RequestMapping("/api/v1/ingest")
@RequiredArgsConstructor
public class IngestionController {
    private final ThoughtEventProducer thoughtEventProducer;
    private final ThoughtEventMapper thoughtEventMapper;

    @Async
    @PostMapping()
    public CompletableFuture<ResponseEntity<String>> createThought(@RequestBody ThoughtDto thoughtDto) {
        thoughtEventProducer.sendThoughtEvent(thoughtEventMapper.toEventFromDto(thoughtDto));
        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.CREATED).body("Thought created!"));
    }

    @Async
    @PostMapping("/bulk")
    public CompletableFuture<ResponseEntity<String>> createThoughtBulk(@RequestBody List<ThoughtDto> thoughtDtos) {
        thoughtDtos.forEach(thoughtDto -> thoughtEventProducer.sendThoughtEvent(thoughtEventMapper.toEventFromDto(thoughtDto)));
        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.CREATED).body("Bulk thoughts created"));
    }

    @GetMapping("/ping")
    public ResponseEntity<String> pingThought() {
        return ResponseEntity.ok().body("Thought ping");
    }
}

