package org.turron.upload.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrameExtractedEvent {
    private String correlationId;
    private String videoId;
    private String frameId;
    private String imageUrl;
}