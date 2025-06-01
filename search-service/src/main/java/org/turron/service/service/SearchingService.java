package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.turron.service.entity.HashEntity;
import org.turron.service.entity.MatchEntity;
import org.turron.service.event.FrameHashedEvent;
import org.turron.service.repository.MatchRepository;
import org.turron.service.repository.HashRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchingService {
    private final HashRepository hashRepository;
    private final MatchRepository matchRepository;

    public void storeHash(FrameHashedEvent event) {
        HashEntity hashEntity = new HashEntity();

        hashEntity.setVideoId(event.getVideoId());
        hashEntity.setFrameId(event.getFrameId());
        hashEntity.setFrameHash(event.getFrameHash());

        hashRepository.save(hashEntity);
    }

    public void findMostSimilarVideo(FrameHashedEvent event) {
        String videoId = event.getVideoId();
        Instant start = Instant.now();
        MatchEntity entity = new MatchEntity();

        try {
            List<String> uploadedVideoHashes = Optional.ofNullable(hashRepository.findHashesByVideoId(videoId))
                    .orElse(Collections.emptyList());

            log.info("Start searching most similar video. Uploaded hashes count: {}", uploadedVideoHashes.size());
            if (uploadedVideoHashes.isEmpty()) {
                log.warn("Uploaded hashes list is empty or null. Aborting search.");
                return;
            }

            List<String> allVideoIds = Optional.ofNullable(hashRepository.findDistinctVideoIds())
                    .orElse(Collections.emptyList());

            if (allVideoIds.isEmpty()) {
                log.warn("No videos found in database for comparison.");
                return;
            }

            Map<String, List<Double>> matchScores = new HashMap<>();

            for (String dbVideoId : allVideoIds) {
                if (dbVideoId.equals(videoId)) continue;

                List<String> dbHashes = Optional.ofNullable(hashRepository.findHashesByVideoId(dbVideoId))
                        .orElse(Collections.emptyList());

                if (dbHashes.isEmpty()) {
                    log.debug("No hashes found for video {}. Skipping.", dbVideoId);
                    continue;
                }

                List<Double> distances = HammingDistance.calculateAllDistances(uploadedVideoHashes, dbHashes);
                matchScores.put(dbVideoId, distances);
            }

            Optional<VideoMatchScoring.MatchResult> bestMatch = VideoMatchScoring.findBestMatch(matchScores);

            if (bestMatch.isPresent()) {
                VideoMatchScoring.MatchResult result = bestMatch.get();
                log.info("Best match found: videoId={}, distance={}", result.videoId(), result.score());

                entity.setVideoId(videoId);
                entity.setMatchedVideoId(result.videoId());
                entity.setScore(result.score());
                matchRepository.save(entity);
                log.info("Matched result saved!");
            } else {
                log.info("No suitable match found.");
            }

        } catch (Exception e) {
            log.error("Exception during video search", e);
        } finally {
            Duration duration = Duration.between(start, Instant.now());
            log.info("Search finished in {} ms", duration.toMillis());
        }
    }
}
