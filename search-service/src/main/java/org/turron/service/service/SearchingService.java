package org.turron.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.turron.service.event.FrameHashedEvent;
import org.turron.service.event.VideoMatchedEvent;
import org.turron.service.repository.SearchRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchingService {

    private final SearchRepository searchRepository;
    private final KafkaTemplate<String, VideoMatchedEvent> kafkaTemplate;

    public void findMostSimilarVideo(FrameHashedEvent event) {
        String correlationId = event.getCorrelationId();
        String videoId = event.getVideoId();

        MDC.put("correlationId", correlationId);
        Instant start = Instant.now();

        try {
            List<String> uploadedVideoHashes = Optional.ofNullable(searchRepository.findHashesByVideoId(videoId))
                    .orElse(Collections.emptyList());

            log.info("Start searching most similar video. Uploaded hashes count: {}", uploadedVideoHashes.size());

            if (uploadedVideoHashes.isEmpty()) {
                log.warn("Uploaded hashes list is empty or null. Aborting search.");
                return;
            }

            List<String> allVideoIds = Optional.ofNullable(searchRepository.findDistinctVideoIds())
                    .orElse(Collections.emptyList());

            if (allVideoIds.isEmpty()) {
                log.warn("No videos found in database for comparison.");
                return;
            }

            String bestVideoId = null;
            double bestScore = Double.MAX_VALUE;

            for (String dbVideoId : allVideoIds) {
                if (dbVideoId.equals(videoId)) continue;

                List<String> dbHashes = Optional.ofNullable(searchRepository.findHashesByVideoId(dbVideoId))
                        .orElse(Collections.emptyList());

                if (dbHashes.isEmpty()) {
                    log.debug("No hashes found for video {}. Skipping.", dbVideoId);
                    continue;
                }

                double avgDistance = HammingDistance.calculateAverageHammingDistance(uploadedVideoHashes, dbHashes);
                log.debug("Video {} average Hamming distance: {}", dbVideoId, avgDistance);

                if (avgDistance < bestScore) {
                    bestScore = avgDistance;
                    bestVideoId = dbVideoId;
                }
            }

            if (bestVideoId != null) {
                log.info("Best match found: videoId={}, distance={}", bestVideoId, bestScore);
                VideoMatchedEvent matchedEvent = new VideoMatchedEvent(correlationId, bestVideoId, bestScore);
                kafkaTemplate.send("video.match.found", matchedEvent);
                log.info("Sent VideoMatchedEvent to Kafka topic video.match.found");
            } else {
                log.info("No suitable match found.");
            }

        } catch (Exception e) {
            log.error("Exception during video search", e);
        } finally {
            Duration duration = Duration.between(start, Instant.now());
            log.info("Search finished in {} ms", duration.toMillis());
            MDC.clear();
        }
    }
}
