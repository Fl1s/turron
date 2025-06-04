package org.turron.service.service;

import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class VideoMatchScoring {
    public Optional<MatchResult> findBestMatch(Map<String, List<Double>> matchScores) {
        return matchScores.entrySet().stream()
                .map(entry -> {
                    String videoId = entry.getKey();
                    List<Double> scores = entry.getValue();
                    double trimmedMean = computeTrimmedMean(scores);
                    return new MatchResult(videoId, trimmedMean);
                })
                .sorted(Comparator.comparingDouble(MatchResult::score))
                .findFirst();
    }
    public double computeTrimmedMean(List<Double> scores) {
        if (scores.size() <= 2)
            return scores.stream().mapToDouble(Double::doubleValue).average().orElse(Double.MAX_VALUE);
        scores.sort(Double::compareTo);
        int trim = Math.max(1, scores.size() / 10);
        List<Double> trimmed = scores.subList(trim, scores.size() - trim);
        return trimmed.stream().mapToDouble(Double::doubleValue).average().orElse(Double.MAX_VALUE);
    }
    public record MatchResult(String videoId, double score) {
    }
}

