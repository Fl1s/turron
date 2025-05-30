package org.turron.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrameExtractedEvent {
    private String correlationId;
    private String frameId;
    private String videoId;
    private String frameUrl;
}
