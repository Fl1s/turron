package org.turron.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "matches")
public class MatchEntity {
    @Id
    private String matchId;

    private String videoId;
    private String matchedVideoId;
    private double score;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (matchId == null || matchId.isEmpty()) {
            matchId = UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
    }
}

