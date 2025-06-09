package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.turron.service.entity.SourceEntity;
import org.turron.service.entity.SnippetEntity;
import org.turron.service.entity.MatchEntity;
import org.turron.service.event.SourceFrameHashedEvent;
import org.turron.service.event.SnippetFrameHashedEvent;
import org.turron.service.repository.MatchRepository;
import org.turron.service.repository.SourceRepository;
import org.turron.service.repository.SnippetRepository;

import java.util.*;

/**
 * Service responsible for storing snippet and source hashes,
 * calculating similarity between snippets and sources, and determining the best match.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchingService {

    private final SnippetRepository snippetRepository;
    private final SourceRepository sourceRepository;
    private final MatchRepository matchRepository;

    /**
     * Stores a frame hash associated with a specific snippet video.
     *
     * @param event an event containing the snippet ID, frame ID, and frame hash
     */
    public void storeSnippetHash(SnippetFrameHashedEvent event) {
        SnippetEntity snippetEntity = new SnippetEntity();
        snippetEntity.setSnippetId(event.getSnippetId());
        snippetEntity.setFrameId(event.getFrameId());
        snippetEntity.setFrameHash(event.getFrameHash());
        snippetRepository.save(snippetEntity);
    }

    /**
     * Stores a frame hash associated with a specific source video.
     *
     * @param event an event containing the source ID, frame ID, and frame hash
     */
    public void storeSourceHash(SourceFrameHashedEvent event) {
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceId(event.getSourceId());
        sourceEntity.setFrameId(event.getFrameId());
        sourceEntity.setFrameHash(event.getFrameHash());
        sourceRepository.save(sourceEntity);
    }

    /**
     * Finds the best matching source video for a given snippet ID.
     * <p>
     * Loads snippet hashes, converts to long, then scans all sources in parallel.
     * Calculates similarity using sliding window with early break on bad scores.
     * Maintains top-5 matches sorted by score and persists the best one.
     *
     * @param snippetId the ID of the snippet video to find a match for
     * @return the source ID with the best match
     * @throws IllegalArgumentException if no snippet hashes are found
     * @throws IllegalStateException    if no sources exist or no match is found
     */
    public String findBestMatch(String snippetId) {
        List<Long> snippetHashes = snippetRepository.findHashesBySnippetId(snippetId).stream()
                .map(this::parseHashToLong)
                .toList();

        if (snippetHashes.isEmpty()) {
            throw new IllegalArgumentException("No snippet hashes found");
        }

        List<String> allSourceIds = sourceRepository.findDistinctSourceIds();
        if (allSourceIds.isEmpty()) {
            throw new IllegalStateException("No sources available");
        }

        PriorityQueue<VideoMatchScoring.MatchResult> topMatches = new PriorityQueue<>(5, Comparator.comparingDouble(VideoMatchScoring.MatchResult::score).reversed());

        allSourceIds.parallelStream().forEach(sourceId -> {
            List<Long> sourceHashes = sourceRepository.findHashesBySourceId(sourceId).stream()
                    .map(this::parseHashToLong)
                    .toList();

            if (sourceHashes.size() < snippetHashes.size()) return;

            List<Double> distances = calculateSlidingDistanceWithEarlyExit(snippetHashes, sourceHashes);
            double score = VideoMatchScoring.computeTrimmedMean(distances);

            synchronized (topMatches) {
                if (topMatches.size() < 5 || score < topMatches.peek().score()) {
                    topMatches.offer(new VideoMatchScoring.MatchResult(sourceId, score));
                    if (topMatches.size() > 5) topMatches.poll();
                }
            }
        });

        if (topMatches.isEmpty()) {
            throw new IllegalStateException("No match found");
        }

        VideoMatchScoring.MatchResult bestMatch = topMatches.stream()
                .min(Comparator.comparingDouble(VideoMatchScoring.MatchResult::score))
                .get();

        MatchEntity matchEntity = new MatchEntity();
        matchEntity.setSnippetId(snippetId);
        matchEntity.setMatchedSourceId(bestMatch.snippetId());
        matchEntity.setScore(bestMatch.score());

        matchRepository.save(matchEntity);

        return bestMatch.snippetId();
    }

    /**
     * Compares input hashes to source hashes over sliding windows,
     * with early exit when partial mean distance exceeds threshold.
     *
     * @param fragmentHashes hashes of snippet video converted to longs
     * @param sourceHashes   hashes of candidate source video converted to longs
     * @return list of distances for best sliding window alignment, or MAX_VALUE if none found
     */
    private List<Double> calculateSlidingDistanceWithEarlyExit(List<Long> fragmentHashes, List<Long> sourceHashes) {
        int windowSize = fragmentHashes.size();
        int maxOffset = sourceHashes.size() - windowSize;

        double bestScore = Double.MAX_VALUE;
        List<Double> bestDistances = Collections.emptyList();

        for (int offset = 0; offset <= maxOffset; offset++) {
            List<Double> distances = new ArrayList<>();
            boolean skipWindow = false;

            for (int i = 0; i < windowSize; i++) {
                int dist = HammingDistance.hammingDistance(fragmentHashes.get(i), sourceHashes.get(offset + i));
                distances.add((double) dist);

                if (i >= 3) {
                    double partialMean = distances.subList(0, i + 1).stream().mapToDouble(Double::doubleValue).average().orElse(Double.MAX_VALUE);
                    if (partialMean > 20.0) {
                        skipWindow = true;
                        break;
                    }
                }
            }

            if (skipWindow) continue;

            double score = VideoMatchScoring.computeTrimmedMean(distances);
            if (score < bestScore) {
                bestScore = score;
                bestDistances = distances;
            }
        }

        return bestDistances.isEmpty() ? List.of(Double.MAX_VALUE) : bestDistances;
    }

    private long parseHashToLong(String bString) {
        return Long.parseUnsignedLong(bString, 2);
    }
}