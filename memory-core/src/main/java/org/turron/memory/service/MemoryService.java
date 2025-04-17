package org.turron.memory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.turron.memory.dto.ThoughtDto;
import org.turron.memory.event.ThoughtEvent;
import org.turron.memory.mapper.ThoughtMapper;
import org.turron.memory.repository.MemoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemoryService {
    private final MemoryRepository memoryRepository;
    private final ThoughtMapper thoughtMapper;

    @Transactional
    public void storeThought(ThoughtEvent thoughtEvent){
        memoryRepository.save(thoughtMapper.toEntity(thoughtEvent));
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
