package org.turron.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.turron.service.entity.SourceEntity;

@Repository
public interface SourceRepository extends JpaRepository<SourceEntity, String> {
}
