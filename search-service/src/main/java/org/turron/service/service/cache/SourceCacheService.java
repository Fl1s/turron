package org.turron.service.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.turron.service.repository.SourceRepository;

import java.util.List;

/**
 * Service for caching and retrieving source frame hashes and source IDs.
 * <p>
 * Supports fetching cached hashes by source ID,
 * caching the list of all source IDs,
 * and evicting caches when data changes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SourceCacheService {

    private final SourceRepository sourceRepository;

    /**
     * Retrieves source frame hashes for a given source ID from cache.
     * If cache miss occurs, loads from repository and caches result.
     *
     * @param sourceId the source video identifier
     * @return list of frame hashes as longs parsed from binary strings
     */
    @Cacheable(value = "sourceHashes", key = "#sourceId")
    public List<Long> getHashes(String sourceId) {
        log.debug("Cache miss for sourceHashes with sourceId={}. Loading from repository.", sourceId);
        List<Long> hashes = sourceRepository.findHashesBySourceId(sourceId).stream()
                .map(s -> Long.parseUnsignedLong(s, 2))
                .toList();
        log.debug("Loaded {} hashes for sourceId={}", hashes.size(), sourceId);
        return hashes;
    }

    /**
     * Evicts the cached source hashes for the given source ID.
     * Should be called after source hashes are updated in the database.
     *
     * @param sourceId the source video identifier
     */
    @CacheEvict(value = "sourceHashes", key = "#sourceId")
    public void evict(String sourceId) {
        log.info("Evicted sourceHashes cache for sourceId={}", sourceId);
    }

    /**
     * Retrieves all distinct source IDs from cache.
     * If cache miss occurs, loads from repository and caches result.
     *
     * @return list of all source video identifiers
     */
    @Cacheable(value = "sourceIds")
    public List<String> getAllSourceIds() {
        log.debug("Cache miss for sourceIds. Loading all distinct source IDs from repository.");
        List<String> sourceIds = sourceRepository.findDistinctSourceIds();
        log.debug("Loaded {} source IDs", sourceIds.size());
        return sourceIds;
    }

    /**
     * Evicts the cached list of all source IDs.
     * Should be called after source entries are added or removed.
     */
    @CacheEvict(value = "sourceIds", allEntries = true)
    public void evictSourceIdCache() {
        log.info("Evicted sourceIds cache");
    }
}
