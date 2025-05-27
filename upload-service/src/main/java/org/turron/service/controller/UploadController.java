package org.turron.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
