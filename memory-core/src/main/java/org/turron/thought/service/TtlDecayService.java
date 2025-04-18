package org.turron.thought.service;

import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TtlDecayService {
    public Duration calculateTtl(int importance) {
        if (importance < 1 || importance > 10) {
            throw new IllegalArgumentException("[Importance] must be between 1 and 10");
        }
        return switch (importance) {
            case 1 -> Duration.ofMinutes(10);
            case 2 -> Duration.ofMinutes(20);
            case 3 -> Duration.ofMinutes(30);
            case 4 -> Duration.ofMinutes(45);
            case 5 -> Duration.ofHours(1);
            case 6 -> Duration.ofHours(2);
            case 7 -> Duration.ofHours(4);
            case 8 -> Duration.ofHours(8);
            case 9 -> Duration.ofHours(12);
            case 10 -> Duration.ofHours(24);
            default -> Duration.ofMinutes(5);
        };
    }
}
