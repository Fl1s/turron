package org.turron.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VideoUploadedEvent {
    private String correlationId;
    private String videoId;
    private String sourceUrl;
}
