package org.turron.service.service;

import lombok.experimental.UtilityClass;

import java.util.*;

/**
 * Utility class responsible for scoring video matches based on calculated distances
 * between hashes and selecting the best match.
 */
@UtilityClass
public class VideoMatchScoring {

    /**
     * Finds the best match (i.e., source video) from the provided match scores.
     *
     * @param matchScores a map of source video IDs to lists of Hamming distances
     * @return an {@link Optional} containing the best match, or empty if no match found
     */
    public Optional<MatchResult> findBestMatch(Map<String, List<Double>> matchScores) {
        return matchScores.entrySet().stream()
                .map(entry -> {
                    String snippetId = entry.getKey();
                    List<Double> scores = entry.getValue();
                    double trimmedMean = computeTrimmedMean(scores);
                    return new MatchResult(snippetId, trimmedMean);
                })
                .sorted(Comparator.comparingDouble(MatchResult::score))
                .findFirst();
    }

    /**
     * Computes a trimmed mean (average) of the given scores, excluding the top and bottom 10%.
     *
     * @param scores list of distance scores
     * @return the trimmed mean value
     */
    public double computeTrimmedMean(List<Double> scores) {
        if (scores.size() <= 2)
            return scores.stream().mapToDouble(Double::doubleValue).average().orElse(Double.MAX_VALUE);

        scores.sort(Double::compareTo);
        int trim = Math.max(1, scores.size() / 10);
        List<Double> trimmed = scores.subList(trim, scores.size() - trim);
        return trimmed.stream().mapToDouble(Double::doubleValue).average().orElse(Double.MAX_VALUE);
    }

    /**
     * Record representing the result of a video match, containing the matched snippet ID and score.
     *
     * @param snippetId the matched source snippet ID
     * @param score   the similarity score (lower means better match)
     */
    public record MatchResult(String snippetId, double score) {}
}