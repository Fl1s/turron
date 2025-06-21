package org.turron.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.turron.service.service.MinioService;
import org.turron.service.service.SearchingService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchingService searchingService;
    private final MinioService minioService;

    @GetMapping(value = "/best-match/{snippetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> bestMatch(@PathVariable String snippetId) {
        return ResponseEntity.ok(Map.of("downloadUrl",
                minioService.generatePreSignedUrl("sources/" +
                        searchingService.findBestMatch(snippetId))));
    }
}
