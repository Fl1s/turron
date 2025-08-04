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
@Table(name = "sources")
public class SourceEntity {
    @Id
    private String sourceId;

    @Column(length = 2048)
    private String sourceUrl;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (sourceId == null || sourceId.isEmpty()) {
            sourceId = UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
    }
}
