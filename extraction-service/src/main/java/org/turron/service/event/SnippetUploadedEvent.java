package org.turron.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnippetUploadedEvent {
    private String correlationId;
    private String snippetId;
    private String sourceUrl;
}
