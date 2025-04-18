package org.turron.thought.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.turron.thought.dto.ThoughtDto;
import org.turron.thought.entity.ThoughtEntity;
import org.turron.thought.event.ThoughtEvent;
import org.turron.thought.mapper.ThoughtMapper;
import org.turron.thought.producer.ThoughtResultProducer;
import org.turron.thought.repository.MemoryRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryService {
    private final MemoryRepository memoryRepository;
    private final ThoughtMapper thoughtMapper;
    private final ThoughtResultProducer thoughtResultProducer;
    private final TtlDecayService ttlDecayService;

    @Transactional
    public void storeThought(ThoughtEvent thoughtEvent) {
        try {
            thoughtResultProducer.sendResult(thoughtEvent, true);
            ThoughtEntity entity = thoughtMapper.toEntity(thoughtEvent);

            Instant created = Optional.ofNullable(thoughtEvent.getCreatedAt())
                    .orElse(Instant.now());
            entity.setCreatedAt(created);

            Instant expires = created.plus(ttlDecayService.calculateTtl(thoughtEvent.getImportance()));
            entity.setExpiresAt(expires);

            log.info("[Memory] Saving thought. correlationId={}, expiresAt={}",
                    thoughtEvent.getCorrelationId(), expires);
            memoryRepository.save(entity);

            log.info("[Memory] Thought stored successfully. correlationId={}", thoughtEvent.getCorrelationId());
            thoughtResultProducer.sendResult(thoughtEvent, true);
        } catch (Exception e) {
            log.error("[Memory] Failed to store thought. correlationId={}", thoughtEvent.getCorrelationId(), e);
            thoughtResultProducer.sendResult(thoughtEvent, false);
        }
    }

    public Optional<ThoughtDto> getThought(String thoughtId) {
        return memoryRepository.findById(thoughtId)
                .map(thoughtMapper::toDto);
    }

    public List<ThoughtDto> searchThoughts(List<String> tags, String type, Integer importance) {
        return memoryRepository.findByTagsOrTypeOrImportance(tags, type, importance).stream()
                .map(thoughtMapper::toDto)
                .toList();
    }

    public void forgetThought(String thoughtId) {
        if (!memoryRepository.existsById(thoughtId)) {
            throw new IllegalArgumentException("[Memory] Thought not found...");
        }
        memoryRepository.deleteById(thoughtId);
    }
}
