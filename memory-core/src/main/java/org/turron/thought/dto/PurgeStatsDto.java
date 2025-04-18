package org.turron.thought.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurgeStatsDto {
    private long totalThoughts;
    private long expiredThoughts;
}
