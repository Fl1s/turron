package org.turron.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "frames")
public class FrameEntity {
    @Id
    private String frameId;

    private String videoId;

    private String frameUrl;

    @PrePersist
    public void prePersist() {
        if (frameId == null || frameId.isEmpty()) {
            frameId = UUID.randomUUID().toString();
        }
    }
}
