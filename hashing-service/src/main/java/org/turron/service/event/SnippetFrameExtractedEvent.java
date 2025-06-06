package org.turron.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SnippetFrameExtractedEvent {
    private String frameId;
    private String snippetId;
    private String frameUrl;
    private String correlationId;
}