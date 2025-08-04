package org.turron.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "snippets")
public class SnippetEntity {
    @Id
    private String snippetId;

    @Column(length = 2048)
    private String sourceUrl;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (snippetId == null || snippetId.isEmpty()) {
            snippetId = UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
    }
}
