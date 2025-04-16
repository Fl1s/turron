package org.turron.ingestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThoughtDto {
    private String source;
    private String type;
    private String content;

    private List<String> tags;

    private Instant createdAt;
    private Instant expiresAt;

    private Integer importance;
}
