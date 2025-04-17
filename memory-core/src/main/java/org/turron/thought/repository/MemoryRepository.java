package org.turron.thought.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.turron.thought.entity.ThoughtEntity;

import java.util.List;

@Repository
public interface MemoryRepository extends MongoRepository<ThoughtEntity, String> {
    List<ThoughtEntity> findByTagsOrTypeOrImportance(List<String> tags, String type, Integer importance);
}
