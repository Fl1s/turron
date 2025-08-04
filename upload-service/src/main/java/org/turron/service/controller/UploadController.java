package org.turron.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.turron.service.dto.SourceDto;
import org.turron.service.dto.SnippetDto;
import org.turron.service.service.UploadService;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/upload")
public class UploadController {
    private final UploadService uploadService;

    @PostMapping("/snippet")
    public ResponseEntity<SnippetDto> uploadSnippet(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(201)
                .body(uploadService.uploadSnippet(file));
    }

    @PostMapping("/source")
    public ResponseEntity<SourceDto> uploadSource(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(201)
                .body(uploadService.uploadSource(file));
    }
}
