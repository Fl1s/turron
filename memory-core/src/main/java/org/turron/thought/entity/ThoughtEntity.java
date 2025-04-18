package org.turron.thought.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "thoughts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThoughtEntity {
    @Id
    private String thoughtId;
    private String source;
    private String type;
    private String content;

    private List<String> tags;

    private Instant createdAt;
    private Instant expiresAt;

    private Integer importance;
}

