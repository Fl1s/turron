package org.turron.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.turron.service.entity.MatchEntity;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, String> {
}
