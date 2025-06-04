package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.turron.service.entity.SourceEntity;
import org.turron.service.entity.VideoEntity;
import org.turron.service.entity.MatchEntity;
import org.turron.service.event.SourceFrameHashedEvent;
import org.turron.service.event.VideoFrameHashedEvent;
import org.turron.service.repository.MatchRepository;
import org.turron.service.repository.SourceRepository;
import org.turron.service.repository.VideoRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchingService {
    private final VideoRepository videoRepository;
    private final SourceRepository sourceRepository;
    private final MatchRepository matchRepository;

    public void storeVideoHash(VideoFrameHashedEvent event) {
        VideoEntity videoEntity = new VideoEntity();

        videoEntity.setVideoId(event.getVideoId());
        videoEntity.setFrameId(event.getFrameId());
        videoEntity.setFrameHash(event.getFrameHash());

        videoRepository.save(videoEntity);
    }
    public void storeSourceHash(SourceFrameHashedEvent event) {
        SourceEntity sourceEntity = new SourceEntity();

        sourceEntity.setSourceId(event.getSourceId());
        sourceEntity.setFrameId(event.getFrameId());
        sourceEntity.setFrameHash(event.getFrameHash());

        sourceRepository.save(sourceEntity);
    }

    public void findMostSimilarSource(VideoFrameHashedEvent event) {
        String videoId = event.getVideoId();
        Instant start = Instant.now();
        MatchEntity match = new MatchEntity();

        try {
            List<String> videoHashes = Optional.ofNullable(videoRepository.findHashesByVideoId(videoId))
                    .orElse(Collections.emptyList());

            if (videoHashes.isEmpty()) {
                log.warn("No hashes found for video {}", videoId);
                return;
            }

            List<String> allSourceIds = Optional.ofNullable(sourceRepository.findDistinctSourceIds())
                    .orElse(Collections.emptyList());

            if (allSourceIds.isEmpty()) {
                log.warn("No sources available in DB.");
                return;
            }

            Map<String, List<Double>> matchScores = new HashMap<>();

            for (String sourceId : allSourceIds) {
                List<String> sourceHashes = Optional.ofNullable(sourceRepository.findHashesBySourceId(sourceId))
                        .orElse(Collections.emptyList());

                if (sourceHashes.size() < videoHashes.size()) {
                    log.debug("Skipping source {} due to insufficient hash count", sourceId);
                    continue;
                }

                List<Double> distances = calculateSlidingDistance(videoHashes, sourceHashes);
                matchScores.put(sourceId, distances);
            }

            Optional<VideoMatchScoring.MatchResult> bestMatch = VideoMatchScoring.findBestMatch(matchScores);

            if (bestMatch.isPresent()) {
                VideoMatchScoring.MatchResult result = bestMatch.get();
                log.info("Best source match: sourceId={}, score={}", result.videoId(), result.score());

                match.setVideoId(videoId);
                match.setMatchedSourceId(result.videoId());
                match.setScore(result.score());
                matchRepository.save(match);
                log.info("Match result saved successfully.");
            } else {
                log.info("No match found.");
            }

        } catch (Exception e) {
            log.error("Error occurred during matching", e);
        } finally {
            log.info("Matching completed in {} ms", Duration.between(start, Instant.now()).toMillis());
        }
    }

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
