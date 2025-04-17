package org.turron.memory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.turron.memory.dto.ThoughtDto;
import org.turron.memory.event.ThoughtEvent;
import org.turron.memory.mapper.ThoughtMapper;
import org.turron.memory.producer.ThoughtResultProducer;
import org.turron.memory.repository.MemoryRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryService {
    private final MemoryRepository memoryRepository;
    private final ThoughtMapper thoughtMapper;
    private final ThoughtResultProducer thoughtResultProducer;

    @Transactional
    public void storeThought(ThoughtEvent thoughtEvent){
        try {
            memoryRepository.save(thoughtMapper.toEntity(thoughtEvent));
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
            throw new IllegalArgumentException("[ Thought not found... ]");
        }
        memoryRepository.deleteById(thoughtId);
    }
}
