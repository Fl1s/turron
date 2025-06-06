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
     * Finds the best matching source video for the given input snippet ID.
     *
     * @param snippetId the ID of the input snippet video
     * @return the ID of the best matching source video
     * @throws IllegalArgumentException if no hashes are found for the input video
     * @throws IllegalStateException if no sources are available or no match is found
     */
    public String findBestMatch(String snippetId) {
        List<String> snippetHashes = Optional.ofNullable(snippetRepository.findHashesBySnippetId(snippetId))
                .orElse(Collections.emptyList());

        if (snippetHashes.isEmpty()) {
            log.warn("No hashes found for snippet {}", snippetId);
            throw new IllegalArgumentException("No snippet hashes found");
        }

        List<String> allSourceIds = Optional.ofNullable(sourceRepository.findDistinctSourceIds())
                .orElse(Collections.emptyList());

        if (allSourceIds.isEmpty()) {
            log.warn("No sources available in DB.");
            throw new IllegalStateException("No sources available");
        }

        Map<String, List<Double>> matchScores = new HashMap<>();

        for (String sourceId : allSourceIds) {
            List<String> sourceHashes = Optional.ofNullable(sourceRepository.findHashesBySourceId(sourceId))
                    .orElse(Collections.emptyList());

            if (sourceHashes.size() < snippetHashes.size()) {
                log.debug("Skipping source {} due to insufficient hash count", sourceId);
                continue;
            }

            List<Double> distances = calculateSlidingDistance(snippetHashes, sourceHashes);
            matchScores.put(sourceId, distances);
        }

        Optional<VideoMatchScoring.MatchResult> bestMatchOpt = VideoMatchScoring.findBestMatch(matchScores);

        if (bestMatchOpt.isEmpty()) {
            throw new IllegalStateException("No match found");
        }

        VideoMatchScoring.MatchResult bestMatch = bestMatchOpt.get();

        MatchEntity matchEntity = new MatchEntity();
        matchEntity.setSnippetId(snippetId);
        matchEntity.setMatchedSourceId(bestMatch.snippetId());
        matchEntity.setScore(bestMatch.score());

        matchRepository.save(matchEntity);

        log.info("Saved match: snippet={} matched with source={} (score={})",
                snippetId, bestMatch.snippetId(), bestMatch.score());

        return bestMatch.snippetId();
    }

    /**
     * Calculates the best alignment of the input hashes over a sliding window of the source hashes.
     * Used to compare sequences of hashes with different starting positions.
     *
     * @param fragmentHashes the hash sequence of the input snippet video
     * @param sourceHashes the hash sequence of a candidate source video
     * @return the list of distances representing the best sliding window match
     */
    private List<Double> calculateSlidingDistance(List<String> fragmentHashes, List<String> sourceHashes) {
        int windowSize = fragmentHashes.size();
        int maxOffset = sourceHashes.size() - windowSize;

        if (maxOffset < 0) {
            return Collections.singletonList(Double.MAX_VALUE);
        }

        double bestScore = Double.MAX_VALUE;
        List<Double> bestDistances = Collections.emptyList();

        for (int offset = 0; offset <= maxOffset; offset++) {
            List<String> window = sourceHashes.subList(offset, offset + windowSize);
            List<Double> distances = HammingDistance.calculateAllDistances(fragmentHashes, window);
            double score = VideoMatchScoring.computeTrimmedMean(distances);

            if (score < bestScore) {
                bestScore = score;
                bestDistances = distances;
            }
        }

        return bestDistances;
    }
}