package org.turron.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.turron.service.dto.VideoDto;
import org.turron.service.service.UploadService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/upload")
public class UploadController {
    private final UploadService uploadService;
    @PostMapping
    public ResponseEntity<VideoDto> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(201)
                .body(uploadService.upload(file));
    }
}
