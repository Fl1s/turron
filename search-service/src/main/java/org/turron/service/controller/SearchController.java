package org.turron.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.turron.service.service.MinioService;
import org.turron.service.service.SearchingService;

import java.io.File;
import java.io.FileInputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {
    private final SearchingService searchingService;
    private final MinioService minioService;

    @GetMapping("/best-match/{videoId}")
    public ResponseEntity<InputStreamResource> bestMatch(@PathVariable String videoId) {
        File videoFile = minioService.downloadVideo(searchingService.findBestMatch(videoId));

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(videoFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + videoFile.getName() + "\"")
                    .contentType(MediaTypeFactory.getMediaType(videoFile.getName())
                            .orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .contentLength(videoFile.length())
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Failed to stream video", e);
        }
    }
}