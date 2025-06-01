package org.turron.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hashes")
public class HashEntity {
    @Id
    private String hashId;

    private String videoId;

    private String frameId;

    private String frameUrl;

    private String frameHash;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (videoId == null || videoId.isEmpty()) {
            videoId = UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
    }
}
