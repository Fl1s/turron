package org.turron.service.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.turron.service.repository.SnippetRepository;

import java.util.List;

/**
 * Service for caching and retrieving snippet frame hashes.
 * <p>
 * Provides methods to fetch cached hashes by snippet ID,
 * and to evict cache entries when underlying data changes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SnippetCacheService {

    private final SnippetRepository snippetRepository;

    /**
     * Retrieves snippet frame hashes for a given snippet ID from cache.
     * If cache miss occurs, loads from repository and caches result.
     *
     * @param snippetId the snippet video identifier
     * @return list of frame hashes as longs parsed from binary strings
     */
    @Cacheable(value = "snippetHashes", key = "#snippetId")
    public List<Long> getHashes(String snippetId) {
        log.debug("Cache miss for snippetHashes with snippetId={}. Loading from repository.", snippetId);
        List<Long> hashes = snippetRepository.findHashesBySnippetId(snippetId).stream()
                .map(s -> Long.parseUnsignedLong(s, 2))
                .toList();
        log.debug("Loaded {} hashes for snippetId={}", hashes.size(), snippetId);
        return hashes;
    }

    /**
     * Evicts the cached snippet hashes for the given snippet ID.
     * Should be called after snippet hashes are updated in the database.
     *
     * @param snippetId the snippet video identifier
     */
    @CacheEvict(value = "snippetHashes", key = "#snippetId")
    public void evict(String snippetId) {
        log.info("Evicted snippetHashes cache for snippetId={}", snippetId);
    }
}
