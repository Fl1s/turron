package org.turron.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadedEvent {
    private String correlationId;
    private String videoId;
    private String sourceUrl;
}
