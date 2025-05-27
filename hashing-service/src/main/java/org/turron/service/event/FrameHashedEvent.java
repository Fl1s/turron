package org.turron.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrameHashedEvent {
    private String correlationId;
    private String hashId;
    private String videoId;
    private String frameId;
    private String frameHash;
}
