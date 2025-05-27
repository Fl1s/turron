package org.turron.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.turron.service.entity.FrameEntity;

@Repository
public interface FrameRepository extends JpaRepository<FrameEntity, String> {
}
