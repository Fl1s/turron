package org.turron.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SnippetFrameHashedEvent {
    private String hashId;
    private String snippetId;
    private String frameId;
    private String frameHash;
    private String correlationId;
}