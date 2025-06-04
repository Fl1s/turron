package org.turron.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceUploadedEvent {
    private String correlationId;
    private String sourceId;
    private String sourceUrl;
}
