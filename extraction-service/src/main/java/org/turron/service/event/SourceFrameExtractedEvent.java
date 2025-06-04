package org.turron.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceFrameExtractedEvent {
    private String correlationId;
    private String frameId;
    private String sourceId;
    private String frameUrl;
}