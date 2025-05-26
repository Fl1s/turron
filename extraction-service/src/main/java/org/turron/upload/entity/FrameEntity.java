package org.turron.upload.entity;

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
    private String videoId;

    private String frameId;

    private String imageUrl;

    @PrePersist
    public void prePersist() {
        if (videoId == null || videoId.isEmpty()) {
            videoId = UUID.randomUUID().toString();
        }
    }
}
